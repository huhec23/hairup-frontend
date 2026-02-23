package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.AllUsersResponse
import com.example.hairup.api.models.ToggleActiveRequest
import com.example.hairup.api.models.ToggleAdminRequest
import com.example.hairup.model.AdminUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUserRepository {

    private val tag = "AdminUserRepository"

    fun getAllUsers(
        token: String, callback: (Result<List<AdminUser>>) -> Unit
    ) {
        Log.d(tag, "Obteniendo todos los usuarios")

        RetrofitClient.apiService.getAllUsers("Bearer $token")
            .enqueue(object : Callback<AllUsersResponse> {
                override fun onResponse(
                    call: Call<AllUsersResponse>, response: Response<AllUsersResponse>
                ) {
                    if (response.isSuccessful) {
                        val users = response.body()?.data?.map { it.toAdminUser() } ?: emptyList()
                        callback(Result.success(users))
                    } else {
                        val errorMsg = try {
                            response.errorBody()?.string() ?: "Error ${response.code()}"
                        } catch (_: Exception) {
                            "Error ${response.code()}"
                        }
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(call: Call<AllUsersResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun toggleAdmin(
        token: String, userId: Int, makeAdmin: Boolean, callback: (Result<Map<String, Any>>) -> Unit
    ) {
        Log.d(tag, "${if (makeAdmin) "Dando" else "Quitando"} admin a usuario $userId")

        val request = ToggleAdminRequest(userId)
        val call = if (makeAdmin) {
            RetrofitClient.apiService.makeAdmin("Bearer $token", request)
        } else {
            RetrofitClient.apiService.removeAdmin("Bearer $token", request)
        }

        call.enqueue(object : Callback<Map<String, Any>> {
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

    fun toggleActive(
        token: String, userId: Int, active: Boolean, callback: (Result<Map<String, Any>>) -> Unit
    ) {
        Log.d(tag, "${if (active) "Habilitando" else "Deshabilitando"} usuario $userId")

        val request = ToggleActiveRequest(userId, active)

        RetrofitClient.apiService.toggleActive("Bearer $token", request)
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