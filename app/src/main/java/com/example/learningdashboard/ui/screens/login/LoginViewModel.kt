package com.example.learningdashboard.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.R
import com.example.learningdashboard.domain.model.LoginResult
import com.example.learningdashboard.domain.model.UiText
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.domain.usecase.ValidateEmailUseCase
import com.example.learningdashboard.domain.usecase.ValidatePasswordUseCase
import com.example.learningdashboard.ui.screens.login.state.LoginScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: LoginScreenAction) {
        when(action) {
            is LoginScreenAction.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        form= it.form.copy(
                            email = action.email
                        )
                    )
                }
                validateEmail()
            }

            is LoginScreenAction.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        form = it.form.copy(
                            password = action.pass
                        )
                    )
                }
                validatePass()
            }

            is LoginScreenAction.PasswordVisibilityToggle -> {
                _uiState.update {
                    it.copy(
                        ui = it.ui.copy(
                            isPasswordVisible = action.isVisible
                        )
                    )
                }
            }

            is LoginScreenAction.Submit -> {
                login()
            }
        }
    }

    private fun login() {
        val uiState = _uiState.value
        if (uiState.ui.isLoading || uiState.ui.isLoggedIn) return
        val emailValid = validateEmail()
        val passwordValid = validatePass()
        if (!emailValid || !passwordValid) return

        val form = uiState.form
        _uiState.update { it.copy(ui = it.ui.copy(isLoading = true, error = null)) }

        viewModelScope.launch {
            try {
                val result = authRepository.login(form.email, form.password)
                _uiState.update {
                    it.copy(
                        form = if (result == LoginResult.Success) {
                            it.form.copy(password = "", passwordError = null)
                        } else it.form,
                        ui = it.ui.copy(
                            isLoggedIn = result == LoginResult.Success,
                            error = when (result) {
                                LoginResult.Success -> null
                                LoginResult.InvalidCredentials -> UiText.StringResource(R.string.invalid_credentials)
                                LoginResult.Offline -> UiText.StringResource(R.string.login_offline)
                                LoginResult.ServiceUnavailable -> UiText.StringResource(R.string.login_service_unavailable)
                                LoginResult.PersistenceFailure -> UiText.StringResource(R.string.user_persistence_failure)
                            }
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "An error occurred while logging in", e)
            } finally {
                _uiState.update { it.copy(ui = it.ui.copy(isLoading = false)) }
            }
        }
    }

    private fun validateEmail(): Boolean {
        val result = validateEmailUseCase(uiState.value.form.email)
        _uiState.update {
            it.copy(
                form = it.form.copy(
                    emailError = result.errorMessage
                )
            )
        }
        return result.successful
    }

    private fun validatePass(): Boolean {
        val result = validatePasswordUseCase(uiState.value.form.password)
        _uiState.update {
            it.copy(
                form = it.form.copy(
                    passwordError = result.errorMessage
                )
            )
        }
        return result.successful
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}