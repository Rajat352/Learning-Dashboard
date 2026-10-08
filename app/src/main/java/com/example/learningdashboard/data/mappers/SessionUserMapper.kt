package com.example.learningdashboard.data.mappers

import com.example.learningdashboard.data.dto.SessionUserEntity
import com.example.learningdashboard.domain.model.SessionUser

fun SessionUserEntity.toDomain(): SessionUser {
    return SessionUser(
        userId = this.id,
        name = this.name,
        email = this.email
    )
}