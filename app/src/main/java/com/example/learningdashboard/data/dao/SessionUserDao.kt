package com.example.learningdashboard.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.example.learningdashboard.data.dto.SessionUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionUserDao {

    @Query("SELECT * FROM session_user LIMIT 1")
    fun observeSessionUser(): Flow<SessionUserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createSessionUser(user: SessionUserEntity)

    @Query("DELETE FROM session_user")
    suspend fun clearSessionUser()

    @Query("DELETE FROM courses")
    suspend fun clearUserCourses()

    @Transaction
    suspend fun clearUserDataAndSession() {
        clearUserCourses()
        clearSessionUser()
    }

    @Transaction
    suspend fun replaceSessionUser(user: SessionUserEntity) {
        clearSessionUser()
        createSessionUser(user)
    }
}
