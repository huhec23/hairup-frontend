package com.example.hairup.data.repository

import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.LoginRequest
import com.example.hairup.api.models.RegisterRequest
import com.example.hairup.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository {

    fun login(email: String, password: String, callback: (Result<UserWithToken>) -> Unit) {
        val request = LoginRequest(email, password)

        RetrofitClient.apiService.login(request)
            .enqueue(object : Callback<com.example.hairup.api.models.LoginResponse> {
                override fun onResponse(
                    call: Call<com.example.hairup.api.models.LoginResponse>,
                    response: Response<com.example.hairup.api.models.LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            val user = body.user.toUser()
                            callback(Result.success(UserWithToken(body.token, user)))
                        } else {
                            callback(Result.failure(Exception("Respuesta vacía del servidor")))
                        }
                    } else {
                        val errorMsg = when (response.code()) {
                            401 -> "Email o contraseña incorrectos"
                            403 -> "Acceso denegado"
                            404 -> "Usuario no encontrado"
                            else -> "Error del servidor: ${response.code()}"
                        }
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(
                    call: Call<com.example.hairup.api.models.LoginResponse>,
                    t: Throwable
                ) {
                    val errorMsg = when {
                        t.message?.contains("Failed to connect") == true -> "No se pudo conectar con el servidor"
                        t.message?.contains("timeout") == true -> "Tiempo de espera agotado"
                        else -> "Error de red: ${t.message}"
                    }
                    callback(Result.failure(Exception(errorMsg)))
                }
            })
    }

    fun register(
        email: String,
        password: String,
        name: String,
        phone: String,
        callback: (Result<UserWithToken>) -> Unit
    ) {
        val request = RegisterRequest(email, password, name, phone)

        RetrofitClient.apiService.register(request)
            .enqueue(object : Callback<com.example.hairup.api.models.LoginResponse> {
                override fun onResponse(
                    call: Call<com.example.hairup.api.models.LoginResponse>,
                    response: Response<com.example.hairup.api.models.LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            val user = body.user.toUser()
                            callback(Result.success(UserWithToken(body.token, user)))
                        } else {
                            callback(Result.failure(Exception("Respuesta vacía del servidor")))
                        }
                    } else {
                        val errorMsg = when (response.code()) {
                            409 -> "El email ya está registrado"
                            400 -> "Datos inválidos"
                            else -> "Error del servidor: ${response.code()}"
                        }
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(
                    call: Call<com.example.hairup.api.models.LoginResponse>,
                    t: Throwable
                ) {
                    callback(Result.failure(Exception("Error de red: ${t.message}")))
                }
            })
    }
}

data class UserWithToken(
    val token: String, val user: User
)