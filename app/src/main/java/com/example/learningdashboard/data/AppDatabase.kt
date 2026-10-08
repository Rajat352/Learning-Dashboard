package com.example.learningdashboard.data

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
}