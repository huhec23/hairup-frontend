package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.ChangePasswordRequest
import com.example.hairup.api.models.UpdateProfileRequest
import com.example.hairup.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository {

    private val tag = "ProfileRepository"

    fun getProfile(token: String, callback: (Result<User>) -> Unit) {
        RetrofitClient.apiService.getProfile("Bearer $token")
            .enqueue(object : Callback<com.example.hairup.api.models.UserResponse> {
                override fun onResponse(
                    call: Call<com.example.hairup.api.models.UserResponse>,
                    response: Response<com.example.hairup.api.models.UserResponse>
                ) {
                    Log.d(tag, "getProfile response code: ${response.code()}")

                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            callback(Result.success(userResponse.toUser()))
                        } else {
                            callback(Result.failure(Exception("Respuesta vacía")))
                        }
                    } else {
                        val errorMsg = when (response.code()) {
                            401 -> "Sesión expirada"
                            403 -> "No autorizado"
                            404 -> "Usuario no encontrado"
                            else -> "Error ${response.code()}"
                        }
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(
                    call: Call<com.example.hairup.api.models.UserResponse>,
                    t: Throwable
                ) {
                    Log.e(tag, "Error getProfile", t)
                    callback(Result.failure(Exception("Error de red: ${t.message}")))
                }
            })
    }

    fun updateProfile(
        token: String, name: String, email: String, phone: String, callback: (Result<User>) -> Unit
    ) {
        val request = UpdateProfileRequest(name, email, phone)

        RetrofitClient.apiService.updateProfile("Bearer $token", request)
            .enqueue(object : Callback<com.example.hairup.api.models.UserResponse> {
                override fun onResponse(
                    call: Call<com.example.hairup.api.models.UserResponse>,
                    response: Response<com.example.hairup.api.models.UserResponse>
                ) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            callback(Result.success(userResponse.toUser()))
                        } else {
                            callback(Result.failure(Exception("Respuesta vacía")))
                        }
                    } else {
                        val errorMsg = when (response.code()) {
                            400 -> "Datos inválidos"
                            401 -> "Sesión expirada"
                            409 -> "Email ya en uso"
                            else -> "Error ${response.code()}"
                        }
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(
                    call: Call<com.example.hairup.api.models.UserResponse>,
                    t: Throwable
                ) {
                    callback(Result.failure(Exception("Error de red: ${t.message}")))
                }
            })
    }

    fun changePassword(
        token: String,
        currentPassword: String,
        newPassword: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        val request = ChangePasswordRequest(currentPassword, newPassword)

        RetrofitClient.apiService.changePassword("Bearer $token", request)
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>, response: Response<Map<String, String>>
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(true))
                    } else {
                        val errorMsg = when (response.code()) {
                            400 -> "Contraseña actual incorrecta"
                            401 -> "Sesión expirada"
                            else -> "Error ${response.code()}"
                        }
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    callback(Result.failure(Exception("Error de red: ${t.message}")))
                }
            })
    }
}