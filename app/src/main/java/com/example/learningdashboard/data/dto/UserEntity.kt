package com.example.learningdashboard.data.dto

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val email: String
)
