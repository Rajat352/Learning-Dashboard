package com.example.learningdashboard.ui.screens

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.domain.model.SessionUser
import com.example.learningdashboard.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@Stable
sealed interface AuthState {
    data object Loading: AuthState
    data object Unauthenticated: AuthState
    data class Authenticated(val user: SessionUser): AuthState
    data object Error: AuthState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    authRepository: AuthRepository
): ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.sessionUser
        .map { user ->
            if (user == null) {
                AuthState.Unauthenticated
            } else {
                AuthState.Authenticated(user)
            }
        }
        .catch { emit(AuthState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Loading
        )

}