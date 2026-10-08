package com.example.learningdashboard.data.dto

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "session_user")
data class SessionUserEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val email: String
)
