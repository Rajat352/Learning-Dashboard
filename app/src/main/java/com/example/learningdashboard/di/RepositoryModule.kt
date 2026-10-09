package com.example.learningdashboard.di

import com.example.learningdashboard.data.AppDatabase
import com.example.learningdashboard.data.dao.CourseDao
import com.example.learningdashboard.data.dao.LessonDao
import com.example.learningdashboard.data.dao.SessionUserDao
import com.example.learningdashboard.data.remote.api.AuthApi
import com.example.learningdashboard.data.remote.api.CoursesApi
import com.example.learningdashboard.data.remote.api.LessonsApi
import com.example.learningdashboard.data.repository.AuthRepositoryImpl
import com.example.learningdashboard.data.repository.CourseDetailsRepositoryImpl
import com.example.learningdashboard.data.repository.CoursesRepositoryImpl
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.domain.repository.CourseDetailsRepository
import com.example.learningdashboard.domain.repository.CoursesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSessionUserDao(db: AppDatabase): SessionUserDao {
        return db.sessionUserDao()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        sessionUserDao: SessionUserDao
    ): AuthRepository = AuthRepositoryImpl(api, sessionUserDao)

    @Provides
    @Singleton
    fun provideCourseDao(db: AppDatabase): CourseDao = db.courseDao()

    @Provides
    @Singleton
    fun provideCoursesRepository(
        api: CoursesApi,
        courseDao: CourseDao
    ): CoursesRepository = CoursesRepositoryImpl(api, courseDao)

    @Provides
    @Singleton
    fun provideLessonDao(db: AppDatabase): LessonDao = db.lessonDao()

    @Provides
    @Singleton
    fun provideCourseDetailsRepository(
        api: LessonsApi,
        lessonDao: LessonDao
    ): CourseDetailsRepository = CourseDetailsRepositoryImpl(api, lessonDao)
}