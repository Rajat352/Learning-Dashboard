package com.example.learningdashboard.di

import android.content.Context
import androidx.room3.Room
import com.example.learningdashboard.data.AppDatabase
import com.example.learningdashboard.data.remote.AndroidConnectivityChecker
import com.example.learningdashboard.data.remote.ConnectivityChecker
import com.example.learningdashboard.data.remote.MockApiTransport
import com.example.learningdashboard.data.remote.api.AuthApi
import com.example.learningdashboard.data.remote.api.CoursesApi
import com.example.learningdashboard.data.remote.api.FakeAuthApiImpl
import com.example.learningdashboard.data.remote.api.FakeCoursesApiImpl
import com.example.learningdashboard.data.remote.api.FakeLessonsApiImpl
import com.example.learningdashboard.data.remote.api.LessonsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideConnectivityChecker(checker: AndroidConnectivityChecker): ConnectivityChecker = checker

    @Provides
    @Singleton
    fun provideMockApiTransport(connectivity: ConnectivityChecker): MockApiTransport =
        MockApiTransport(connectivity)

    @Provides
    @Singleton
    fun provideAuthApi(transport: MockApiTransport): AuthApi = FakeAuthApiImpl(transport)

    @Provides
    @Singleton
    fun provideCoursesApi(transport: MockApiTransport): CoursesApi = FakeCoursesApiImpl(transport)

    @Provides
    @Singleton
    fun provideLessonsApi(transport: MockApiTransport): LessonsApi = FakeLessonsApiImpl(transport)
}