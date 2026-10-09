package com.example.learningdashboard.ui.screens.coursedetails

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.R
import com.example.learningdashboard.domain.model.CourseDetailsResult
import com.example.learningdashboard.domain.model.UiText
import com.example.learningdashboard.domain.repository.CourseDetailsRepository
import com.example.learningdashboard.domain.usecase.CalculateCourseProgressUseCase
import com.example.learningdashboard.ui.screens.coursedetails.action.CourseDetailsScreenAction
import com.example.learningdashboard.ui.screens.coursedetails.state.CourseDetailsScreenState
import com.example.learningdashboard.ui.screens.coursedetails.state.CourseDetailsUiState
import com.example.learningdashboard.ui.screens.coursedetails.state.LessonUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CourseDetailsScreenViewModel @Inject constructor(
    private val courseDetailsRepository: CourseDetailsRepository,
    private val calculateCourseProgressUseCase: CalculateCourseProgressUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(CourseDetailsScreenState())
    val uiState = _uiState.asStateFlow()
    private var refreshJob: Job? = null
    private var currentCourseId: Long? = null

    fun onAction(action: CourseDetailsScreenAction) {
        when (action) {
            is CourseDetailsScreenAction.LoadCourse -> loadCourse(action.courseId)
            CourseDetailsScreenAction.Refresh -> refreshLessons()
            is CourseDetailsScreenAction.MarkLessonComplete -> markLessonComplete(action.lessonId)
        }
    }

    private fun loadCourse(courseId: Long) {
        if (currentCourseId != null) {
            check(currentCourseId == courseId) { "This ViewModel already belongs to another course" }
            return
        }
        currentCourseId = courseId
        observeCourseDetails()
        refreshLessons()
    }

    private fun observeCourseDetails() {
        val courseId = currentCourseId ?: return
        viewModelScope.launch {
            courseDetailsRepository.observeCourseDetails(courseId)
                .catch { e ->
                    if (e is CancellationException) throw e
                    Log.e(TAG, "Unable to load cached lessons", e)
                    _uiState.update {
                        it.copy(isCacheLoaded = true, error = UiText.StringResource(R.string.lessons_persistence_failure))
                    }
                }
                .collect { details ->
                    _uiState.update { state ->
                        state.copy(
                            course = details?.course?.let { course ->
                                CourseDetailsUiState(
                                    title = course.title,
                                    lessonCount = course.lessonCount,
                                    completedLessonCount = course.completedLessonCount,
                                    progress = calculateCourseProgressUseCase(course.lessonCount, course.completedLessonCount)
                                )
                            },
                            lessons = details?.lessons?.map { LessonUiState(it.id, it.title, it.completed) } ?: emptyList(),
                            isCacheLoaded = true,
                            isLessonsCached = details?.isLessonsCached == true,
                            error = if (details == null) UiText.StringResource(R.string.course_not_found) else state.error
                        )
                    }
                }
        }
    }

    private fun refreshLessons() {
        val courseId = currentCourseId ?: return
        if (refreshJob?.isActive == true) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        refreshJob = viewModelScope.launch {
            try {
                val result = courseDetailsRepository.refreshLessons(courseId)
                _uiState.update {
                    it.copy(error = if (it.isCacheLoaded && it.course == null) {
                        UiText.StringResource(R.string.course_not_found)
                    } else result.toError())
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unable to refresh lessons", e)
                _uiState.update {
                    it.copy(error = UiText.StringResource(R.string.lessons_service_unavailable))
                }
            } finally {
                if (isActive) _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun markLessonComplete(lessonId: Long) {
        val courseId = currentCourseId ?: return
        val lesson = _uiState.value.lessons.firstOrNull { it.id == lessonId } ?: return
        if (lesson.completed || lessonId in _uiState.value.updatingLessonIds) return
        _uiState.update {
            it.copy(updatingLessonIds = it.updatingLessonIds + lessonId, completionError = null)
        }

        viewModelScope.launch {
            try {
                val result = courseDetailsRepository.markLessonComplete(courseId, lessonId)
                _uiState.update {
                    it.copy(completionError = if (result == CourseDetailsResult.Success) null else {
                        UiText.StringResource(R.string.lesson_completion_failure)
                    })
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unable to complete lesson", e)
                _uiState.update {
                    it.copy(completionError = UiText.StringResource(R.string.lesson_completion_failure))
                }
            } finally {
                if (isActive) _uiState.update { it.copy(updatingLessonIds = it.updatingLessonIds - lessonId) }
            }
        }
    }

    private fun CourseDetailsResult.toError(): UiText? = when (this) {
        CourseDetailsResult.Success -> null
        CourseDetailsResult.Offline -> UiText.StringResource(R.string.lessons_offline)
        CourseDetailsResult.ServiceUnavailable -> UiText.StringResource(R.string.lessons_service_unavailable)
        CourseDetailsResult.PersistenceFailure -> UiText.StringResource(R.string.lessons_persistence_failure)
        CourseDetailsResult.NotFound -> UiText.StringResource(R.string.course_not_found)
    }

    companion object {
        private const val TAG = "CourseDetailsScreenViewModel"
    }
}
