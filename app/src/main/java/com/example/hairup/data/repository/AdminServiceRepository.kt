package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.CreateServiceRequest
import com.example.hairup.api.models.UpdateServiceRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminServiceRepository {

    private val tag = "AdminServiceRepository"

    fun createService(
        token: String, request: CreateServiceRequest, callback: (Result<Map<String, Any>>) -> Unit
    ) {
        Log.d(tag, "Creando servicio: ${request.name}")

        RetrofitClient.apiService.createService("Bearer $token", request)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>, response: Response<Map<String, Any>>
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

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun updateService(
        token: String,
        serviceId: Int,
        request: UpdateServiceRequest,
        callback: (Result<Map<String, Any>>) -> Unit
    ) {
        Log.d(tag, "Actualizando servicio $serviceId")

        RetrofitClient.apiService.updateService("Bearer $token", serviceId, request)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>, response: Response<Map<String, Any>>
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

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun deleteService(
        token: String, serviceId: Int, callback: (Result<Map<String, Any>>) -> Unit
    ) {
        Log.d(tag, "Eliminando servicio $serviceId")

        RetrofitClient.apiService.deleteService("Bearer $token", serviceId)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>, response: Response<Map<String, Any>>
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

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }
}