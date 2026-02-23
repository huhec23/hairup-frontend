package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.api.models.RewardResponse
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.RewardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RewardViewModel(
    private val sessionManager: SessionManager,
    private val repository: RewardRepository = RewardRepository()
) : ViewModel() {

    private val _rewards = MutableStateFlow<List<RewardItem>>(emptyList())
    val rewards: StateFlow<List<RewardItem>> = _rewards

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _redeemSuccess = MutableStateFlow<RedeemResult?>(null)
    val redeemSuccess: StateFlow<RedeemResult?> = _redeemSuccess

    private val _userPoints = MutableStateFlow(0)
    val userPoints: StateFlow<Int> = _userPoints

    private val _userLevelId = MutableStateFlow(1)
    val userLevelId: StateFlow<Int> = _userLevelId

    data class RewardItem(
        val id: Int,
        val name: String,
        val description: String,
        val pointsCost: Int,
        val minLevelId: Int,
        val available: Boolean,
        val canAfford: Boolean = false,
        val hasRequiredLevel: Boolean = false
    )

    data class RedeemResult(
        val success: Boolean, val message: String, val newPoints: Int?
    )

    fun loadRewards() {
        val token = sessionManager.getToken()

        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true

        repository.getRewards(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { rewards ->
                    val currentUser = sessionManager.getUser()
                    if (currentUser != null) {
                        _userPoints.value = currentUser.points
                        _userLevelId.value = currentUser.levelId
                        processRewards(rewards, currentUser.points, currentUser.levelId)
                    } else {
                        _errorMessage.value = "Usuario no encontrado"
                    }
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar recompensas"
                    _isLoading.value = false
                })
            }
        }
    }

    private fun processRewards(rewards: List<RewardResponse>, userPoints: Int, userLevelId: Int) {
        val rewardItems = rewards.map { reward ->
            RewardItem(
                id = reward.id,
                name = reward.name,
                description = reward.description ?: "",
                pointsCost = reward.pointsCost,
                minLevelId = reward.minLevelId,
                available = reward.available,
                canAfford = userPoints >= reward.pointsCost,
                hasRequiredLevel = userLevelId >= reward.minLevelId
            )
        }
        _rewards.value = rewardItems
    }

    fun redeemReward(rewardId: Int) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _redeemSuccess.value = null

        repository.redeemReward(token, rewardId) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    response.newPoints?.let { newPoints ->
                        sessionManager.getUser()?.let { user ->
                            val updatedUser = user.copy(points = newPoints)
                            sessionManager.saveAuthData(token, updatedUser)
                            _userPoints.value = newPoints
                        }
                    }

                    _redeemSuccess.value = RedeemResult(
                        success = true,
                        message = response.message,
                        newPoints = response.newPoints
                    )

                    loadRewards()
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _redeemSuccess.value = RedeemResult(
                        success = false,
                        message = exception.message ?: "Error al canjear",
                        newPoints = null
                    )
                    _isLoading.value = false
                })
            }
        }
    }

    fun resetRedeemSuccess() {
        _redeemSuccess.value = null
    }

}