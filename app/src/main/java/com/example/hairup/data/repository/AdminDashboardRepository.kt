package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.DashboardStatsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardRepository {

    private val tag = "AdminDashboardRepository"

    fun getDashboardStats(
        token: String, callback: (Result<DashboardStatsResponse>) -> Unit
    ) {
        Log.d(tag, "Obteniendo estadísticas del dashboard")

        RetrofitClient.apiService.getDashboardStats("Bearer $token")
            .enqueue(object : Callback<DashboardStatsResponse> {
                override fun onResponse(
                    call: Call<DashboardStatsResponse>, response: Response<DashboardStatsResponse>
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

                override fun onFailure(call: Call<DashboardStatsResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }
}