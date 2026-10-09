package com.example.learningdashboard.ui.screens.courses

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.R
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.CoursesResult
import com.example.learningdashboard.domain.model.UiText
import com.example.learningdashboard.domain.repository.CoursesRepository
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.domain.usecase.CalculateCourseProgressUseCase
import com.example.learningdashboard.ui.screens.courses.action.CoursesScreenAction
import com.example.learningdashboard.ui.screens.courses.state.CourseUiState
import com.example.learningdashboard.ui.screens.courses.state.CoursesScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CoursesScreenViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository,
    private val authRepository: AuthRepository,
    private val calculateCourseProgressUseCase: CalculateCourseProgressUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(CoursesScreenState())
    val uiState = _uiState.asStateFlow()
    private var refreshJob: Job? = null

    init {
        observeCourses()
        refreshCourses()
    }

    fun onAction(action: CoursesScreenAction) {
        when (action) {
            CoursesScreenAction.Refresh -> refreshCourses()
            CoursesScreenAction.Logout -> logout()
        }
    }

    private fun observeCourses() {
        viewModelScope.launch {
            coursesRepository.courses
                .catch { e ->
                    if (e is CancellationException) throw e
                    Log.e(TAG, "Unable to load cached courses", e)
                    _uiState.update {
                        it.copy(
                            isCacheLoaded = true,
                            error = UiText.StringResource(R.string.courses_persistence_failure)
                        )
                    }
                }
                .collect { courses ->
                    _uiState.update {
                        it.copy(courses = courses.map { course -> course.toUiState() }, isCacheLoaded = true)
                    }
                }
        }
    }

    private fun refreshCourses() {
        if (_uiState.value.isLoggingOut || refreshJob?.isActive == true) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        refreshJob = viewModelScope.launch {
            try {
                val result = coursesRepository.refreshCourses()
                _uiState.update {
                    it.copy(
                        error = when (result) {
                            CoursesResult.Success -> null
                            CoursesResult.Offline -> UiText.StringResource(R.string.courses_offline)
                            CoursesResult.ServiceUnavailable -> UiText.StringResource(R.string.courses_service_unavailable)
                            CoursesResult.PersistenceFailure -> UiText.StringResource(R.string.courses_persistence_failure)
                        }
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unable to refresh courses", e)
                _uiState.update {
                    it.copy(error = UiText.StringResource(R.string.courses_service_unavailable))
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun logout() {
        if (_uiState.value.isLoggingOut) return
        _uiState.update { it.copy(isLoggingOut = true, error = null) }
        viewModelScope.launch {
            try {
                refreshJob?.cancelAndJoin()
                authRepository.logout()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unable to log out", e)
                _uiState.update {
                    it.copy(isLoggingOut = false, error = UiText.StringResource(R.string.logout_failure))
                }
            }
        }
    }

    private fun Course.toUiState(): CourseUiState = CourseUiState(
        id = id,
        title = title,
        instructor = instructor,
        lessonCount = lessonCount,
        progress = calculateCourseProgressUseCase(lessonCount, completedLessonCount)
    )

    companion object {
        private const val TAG = "CoursesScreenViewModel"
    }
}
