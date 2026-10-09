package com.example.learningdashboard.data.repository

import com.example.learningdashboard.data.dao.LessonDao
import com.example.learningdashboard.data.mappers.toDomain
import com.example.learningdashboard.data.mappers.toEntity
import com.example.learningdashboard.data.remote.OfflineException
import com.example.learningdashboard.data.remote.api.CourseNotFoundException
import com.example.learningdashboard.data.remote.api.LessonsApi
import com.example.learningdashboard.domain.model.CourseDetails
import com.example.learningdashboard.domain.model.CourseDetailsResult
import com.example.learningdashboard.domain.repository.CourseDetailsRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class CourseDetailsRepositoryImpl(
    private val api: LessonsApi,
    private val lessonDao: LessonDao
) : CourseDetailsRepository {

    override fun observeCourseDetails(courseId: Long): Flow<CourseDetails?> =
        lessonDao.observeCourseDetails(courseId)
            .map { it?.toDomain() }
            .distinctUntilChanged()

    override suspend fun refreshLessons(courseId: Long): CourseDetailsResult {
        val response = try {
            api.getLessons(courseId)
        } catch (e: CourseNotFoundException) {
            return CourseDetailsResult.NotFound
        } catch (e: OfflineException) {
            return CourseDetailsResult.Offline
        } catch (e: IOException) {
            return CourseDetailsResult.ServiceUnavailable
        }

        return try {
            if (lessonDao.refreshLessons(courseId, response.map { it.toEntity() })) {
                CourseDetailsResult.Success
            } else CourseDetailsResult.NotFound
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CourseDetailsResult.PersistenceFailure
        }
    }

    override suspend fun markLessonComplete(courseId: Long, lessonId: Long): CourseDetailsResult {
        return try {
            if (lessonDao.markLessonComplete(courseId, lessonId)) {
                CourseDetailsResult.Success
            } else CourseDetailsResult.NotFound
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CourseDetailsResult.PersistenceFailure
        }
    }
}
