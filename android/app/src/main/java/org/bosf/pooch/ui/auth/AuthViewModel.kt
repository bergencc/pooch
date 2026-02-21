package org.bosf.pooch.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bosf.pooch.data.repository.AuthRepository
import org.bosf.pooch.data.repository.NetworkResult
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")

            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            when (val result = authRepository.login(email.trim(), password)) {
                is NetworkResult.Success -> _uiState.value = AuthUiState(isSuccess = true)
                is NetworkResult.Error   -> _uiState.value = AuthUiState(error = result.message)
                else -> {}
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")

            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(error = "Passwords do not match")

            return
        }

        if (password.length < 8) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 8 characters")

            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)

            when (val result = authRepository.register(name.trim(), email.trim(), password)) {
                is NetworkResult.Success -> _uiState.value = AuthUiState(isSuccess = true)
                is NetworkResult.Error   -> _uiState.value = AuthUiState(error = result.message)
                else -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
