package com.example.learningdashboard.di

import com.example.learningdashboard.data.AppDatabase
import com.example.learningdashboard.data.dao.SessionUserDao
import com.example.learningdashboard.data.remote.api.AuthApi
import com.example.learningdashboard.data.repository.AuthRepositoryImpl
import com.example.learningdashboard.domain.repository.AuthRepository
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
}