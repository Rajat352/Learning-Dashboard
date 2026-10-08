package com.example.learningdashboard.data.repository

import com.example.learningdashboard.data.dao.SessionUserDao
import com.example.learningdashboard.data.dto.SessionUserEntity
import com.example.learningdashboard.data.mappers.toDomain
import com.example.learningdashboard.data.remote.OfflineException
import com.example.learningdashboard.data.remote.api.AuthApi
import com.example.learningdashboard.data.remote.api.InvalidCredentialsException
import com.example.learningdashboard.data.remote.dto.LoginRequestDto
import com.example.learningdashboard.domain.model.SessionUser
import com.example.learningdashboard.domain.model.LoginResult
import com.example.learningdashboard.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val sessionUserDao: SessionUserDao
) : AuthRepository {

    override val sessionUser: Flow<SessionUser?> =
        sessionUserDao.observeSessionUser()
            .map { entity ->
                entity?.toDomain()
            }
            .distinctUntilChanged()

    override suspend fun logout() {
        sessionUserDao.clearSessionUser()
    }

    override suspend fun login(
        email: String,
        password: String
    ): LoginResult {
        val response = try {
            api.login(LoginRequestDto(email.trim(), password))
        } catch (e: InvalidCredentialsException) {
            return LoginResult.InvalidCredentials
        } catch (e: OfflineException) {
            return LoginResult.Offline
        } catch (e: IOException) {
            return LoginResult.ServiceUnavailable
        }

        return try {
            sessionUserDao.replaceSessionUser(
                SessionUserEntity(
                    id = response.userId,
                    name = response.name,
                    email = response.email
                )
            )
            LoginResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoginResult.PersistenceFailure
        }
    }
}
