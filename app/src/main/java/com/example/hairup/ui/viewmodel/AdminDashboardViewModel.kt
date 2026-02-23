package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.AdminDashboardRepository
import com.example.hairup.model.MiniAppointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminDashboardViewModel(
    private val sessionManager: SessionManager,
    private val repository: AdminDashboardRepository = AdminDashboardRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Stats
    private val _totalToday = MutableStateFlow(0)
    val totalToday: StateFlow<Int> = _totalToday

    private val _pendingToday = MutableStateFlow(0)
    val pendingToday: StateFlow<Int> = _pendingToday

    private val _confirmedToday = MutableStateFlow(0)
    val confirmedToday: StateFlow<Int> = _confirmedToday

    private val _totalStylists = MutableStateFlow(0)
    val totalStylists: StateFlow<Int> = _totalStylists

    private val _activeStylists = MutableStateFlow(0)
    val activeStylists: StateFlow<Int> = _activeStylists

    private val _todayAppointments = MutableStateFlow<List<MiniAppointment>>(emptyList())
    val todayAppointments: StateFlow<List<MiniAppointment>> = _todayAppointments

    private val _stylistName = MutableStateFlow("")
    val stylistName: StateFlow<String> = _stylistName

    private val _stylistSpecialty = MutableStateFlow("")
    val stylistSpecialty: StateFlow<String> = _stylistSpecialty

    private val _isGenericAdmin = MutableStateFlow(false)
    val isGenericAdmin: StateFlow<Boolean> = _isGenericAdmin

    init {
        loadUserData()
        loadDashboardStats()
    }

    private fun loadUserData() {
        val user = sessionManager.getUser()
        if (user != null) {
            _stylistName.value = user.name
            _isGenericAdmin.value = user.isAdmin && user.id == 1
            _stylistSpecialty.value =
                if (_isGenericAdmin.value) "Administrador general" else "Peluquero/a"
        }
    }

    fun loadDashboardStats() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true

        repository.getDashboardStats(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { stats ->
                    _totalToday.value = stats.totalToday
                    _pendingToday.value = stats.pendingToday
                    _confirmedToday.value = stats.confirmedToday
                    _totalStylists.value = stats.totalStylists
                    _activeStylists.value = stats.activeStylists
                    _todayAppointments.value =
                        stats.todayAppointments.map { it.toMiniAppointment() }
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar estadísticas"
                    _isLoading.value = false
                })
            }
        }
    }

    fun resetError() {
        _errorMessage.value = null
    }
}