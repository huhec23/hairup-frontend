package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.api.models.CreateServiceRequest
import com.example.hairup.api.models.UpdateServiceRequest
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.AdminServiceRepository
import com.example.hairup.data.repository.BookingRepository
import com.example.hairup.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminServiceViewModel(
    private val sessionManager: SessionManager,
    private val bookingRepository: BookingRepository = BookingRepository(),
    private val adminRepository: AdminServiceRepository = AdminServiceRepository()
) : ViewModel() {

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess: StateFlow<Boolean> = _operationSuccess

    init {
        loadServices()
    }

    fun loadServices() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true

        bookingRepository.getServices(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { serviceResponses ->
                    _services.value = serviceResponses.map { it.toService() }
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar servicios"
                    _isLoading.value = false
                })
            }
        }
    }

    fun createService(
        name: String, description: String, price: Double, duration: Int, xp: Int
    ) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false

        val request = CreateServiceRequest(
            name = name,
            description = description.takeIf { it.isNotBlank() },
            price = price,
            duration = duration,
            xp = xp
        )

        adminRepository.createService(token, request) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    _successMessage.value = response["message"] as? String ?: "Servicio creado"
                    _operationSuccess.value = true
                    loadServices()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al crear servicio"
                    _isLoading.value = false
                })
            }
        }
    }

    fun updateService(
        serviceId: Int, name: String, description: String, price: Double, duration: Int, xp: Int
    ) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false

        val request = UpdateServiceRequest(
            name = name.takeIf { it.isNotBlank() },
            description = description.takeIf { it.isNotBlank() },
            price = price,
            duration = duration,
            xp = xp
        )

        adminRepository.updateService(token, serviceId, request) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    _successMessage.value = response["message"] as? String ?: "Servicio actualizado"
                    _operationSuccess.value = true
                    loadServices()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al actualizar servicio"
                    _isLoading.value = false
                })
            }
        }
    }

    fun deleteService(serviceId: Int) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false

        adminRepository.deleteService(token, serviceId) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    _successMessage.value = response["message"] as? String ?: "Servicio eliminado"
                    _operationSuccess.value = true
                    loadServices()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al eliminar servicio"
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
}