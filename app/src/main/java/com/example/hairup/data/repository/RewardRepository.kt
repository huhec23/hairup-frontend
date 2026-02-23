package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.RedeemRequest
import com.example.hairup.api.models.RedeemResponse
import com.example.hairup.api.models.RewardResponse
import com.example.hairup.api.models.RewardsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RewardRepository {

    private val tag = "RewardRepository"

    fun getRewards(token: String, callback: (Result<List<RewardResponse>>) -> Unit) {
        Log.d(tag, "Obteniendo recompensas")

        RetrofitClient.apiService.getRewards("Bearer $token")
            .enqueue(object : Callback<RewardsResponse> {
                override fun onResponse(
                    call: Call<RewardsResponse>, response: Response<RewardsResponse>
                ) {
                    if (response.isSuccessful) {
                        val rewards = response.body()?.data ?: emptyList()
                        callback(Result.success(rewards))
                    } else {
                        callback(Result.failure(Exception("Error ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<RewardsResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun redeemReward(
        token: String, rewardId: Int, callback: (Result<RedeemResponse>) -> Unit
    ) {
        Log.d(tag, "Canjeando recompensa $rewardId")

        val request = RedeemRequest(rewardId)

        RetrofitClient.apiService.redeemReward("Bearer $token", request)
            .enqueue(object : Callback<RedeemResponse> {
                override fun onResponse(
                    call: Call<RedeemResponse>, response: Response<RedeemResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { callback(Result.success(it)) }
                            ?: callback(Result.failure(Exception("Respuesta vacía")))
                    } else {
                        val errorMsg = try {
                            response.errorBody()?.string() ?: "Error ${response.code()}"
                        } catch (_: Exception) {
                            "Error ${response.code()}"
                        }
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(call: Call<RedeemResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }
}