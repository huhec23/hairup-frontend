package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.AdminUserRepository
import com.example.hairup.model.AdminUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminUserViewModel(
    private val sessionManager: SessionManager,
    private val repository: AdminUserRepository = AdminUserRepository()
) : ViewModel() {

    private val _users = MutableStateFlow<List<AdminUser>>(emptyList())
    val users: StateFlow<List<AdminUser>> = _users

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess: StateFlow<Boolean> = _operationSuccess

    private val currentUserId = sessionManager.getUser()?.id ?: 0

    init {
        loadUsers()
    }

    fun loadUsers() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true

        repository.getAllUsers(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { users ->
                    _users.value = users
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar usuarios"
                    _isLoading.value = false
                })
            }
        }
    }

    fun toggleAdmin(userId: Int, makeAdmin: Boolean) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        if (userId == currentUserId) {
            _errorMessage.value = "No puedes modificar tu propio estado de administrador"
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false

        repository.toggleAdmin(token, userId, makeAdmin) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    _successMessage.value = response["message"] as? String
                        ?: if (makeAdmin) "Admin agregado" else "Admin removido"
                    _operationSuccess.value = true
                    loadUsers()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cambiar permisos"
                    _isLoading.value = false
                })
            }
        }
    }

    fun toggleActive(userId: Int, active: Boolean) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        if (userId == currentUserId) {
            _errorMessage.value = "No puedes modificar tu propio estado"
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false

        repository.toggleActive(token, userId, active) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    _successMessage.value = response["message"] as? String
                        ?: if (active) "Usuario habilitado" else "Usuario deshabilitado"
                    _operationSuccess.value = true
                    loadUsers()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cambiar estado"
                    _isLoading.value = false
                })
            }
        }
    }

    fun resetStates() {
        _errorMessage.value = null
        _successMessage.value = null
        _operationSuccess.value = false
        _isLoading.value = false
    }

    fun getFilteredUsers(searchQuery: String, filter: String): List<AdminUser> {
        return _users.value.filter { user ->
                when (filter) {
                    "Clientes" -> !user.isAdmin
                    "Admins" -> user.isAdmin
                    "Deshabilitados" -> !user.isActive
                    else -> true
                }
            }.filter { user ->
                searchQuery.isBlank() || user.name.contains(
                    searchQuery,
                    ignoreCase = true
                ) || user.email.contains(searchQuery, ignoreCase = true)
            }
    }
}