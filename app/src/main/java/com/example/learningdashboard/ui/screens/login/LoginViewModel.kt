package com.example.learningdashboard.ui.screens.login

import androidx.lifecycle.ViewModel
import com.example.learningdashboard.domain.usecase.ValidateEmailUseCase
import com.example.learningdashboard.domain.usecase.ValidatePasswordUseCase
import com.example.learningdashboard.ui.screens.login.state.LoginScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<LoginUiEvent>()
    val events = _events.receiveAsFlow()

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
                if (validateEmail() && validatePass()) {
                    // Todo
                }
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
}