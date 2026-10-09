package com.example.learningdashboard.data.repository

import com.example.learningdashboard.data.dao.CourseDao
import com.example.learningdashboard.data.mappers.toDomain
import com.example.learningdashboard.data.mappers.toEntity
import com.example.learningdashboard.data.remote.OfflineException
import com.example.learningdashboard.data.remote.api.CoursesApi
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.CoursesResult
import com.example.learningdashboard.domain.repository.CoursesRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class CoursesRepositoryImpl(
    private val api: CoursesApi,
    private val courseDao: CourseDao
) : CoursesRepository {

    override val courses: Flow<List<Course>> = courseDao.observeCourses()
        .map { entities -> entities.map { it.toDomain() } }
        .distinctUntilChanged()

    override suspend fun refreshCourses(): CoursesResult {
        val response = try {
            api.getCourses()
        } catch (e: OfflineException) {
            return CoursesResult.Offline
        } catch (e: IOException) {
            return CoursesResult.ServiceUnavailable
        }

        return try {
            courseDao.refreshCourses(response.map { it.toEntity() })
            CoursesResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CoursesResult.PersistenceFailure
        }
    }
}
