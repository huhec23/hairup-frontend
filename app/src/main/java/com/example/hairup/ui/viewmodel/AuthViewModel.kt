package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    fun login(email: String, password: String) {
        _loginState.value = AuthState.Loading

        repository.login(email, password) { result ->
            viewModelScope.launch {
                result.fold(
                    onSuccess = { userWithToken ->
                        sessionManager.saveAuthData(userWithToken.token, userWithToken.user)
                        _loginState.value = AuthState.Success(userWithToken.user.isAdmin)
                    },
                    onFailure = { exception ->
                        _loginState.value = AuthState.Error(exception.message ?: "Error desconocido")
                    }
                )
            }
        }
    }

    fun register(email: String, password: String, name: String, phone: String) {
        _registerState.value = AuthState.Loading

        repository.register(email, password, name, phone) { result ->
            viewModelScope.launch {
                result.fold(
                    onSuccess = { userWithToken ->
                        sessionManager.saveAuthData(userWithToken.token, userWithToken.user)
                        _registerState.value = AuthState.Success(userWithToken.user.isAdmin)
                    },
                    onFailure = { exception ->
                        _registerState.value = AuthState.Error(exception.message ?: "Error desconocido")
                    }
                )
            }
        }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val isAdmin: Boolean) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}