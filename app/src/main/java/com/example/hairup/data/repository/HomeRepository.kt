package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.AppointmentResponse
import com.example.hairup.api.models.LevelsResponse
import com.example.hairup.model.Level
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository {

    private val tag = "HomeRepository"

    fun getNextAppointment(token: String, callback: (Result<AppointmentResponse?>) -> Unit) {
        Log.d(tag, "Llamando a getNextAppointment")
        RetrofitClient.apiService.getNextAppointment("Bearer $token")
            .enqueue(object : Callback<AppointmentResponse> {
                override fun onResponse(
                    call: Call<AppointmentResponse>, response: Response<AppointmentResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            Log.d(tag, "getNextAppointment exitoso, hay cita: ${body.serviceName}")
                            callback(Result.success(body))
                        } else {
                            Log.d(
                                tag,
                                "getNextAppointment: respuesta 200 pero sin datos válidos, tratando como sin cita"
                            )
                            callback(Result.success(null))
                        }
                    } else if (response.code() == 404) {
                        Log.d(tag, "getNextAppointment: 404 - No hay citas")
                        callback(Result.success(null))
                    } else {
                        val errorMsg = try {
                            response.errorBody()?.string() ?: "Error ${response.code()}"
                        } catch (_: Exception) {
                            "Error ${response.code()}"
                        }
                        Log.e(tag, "getNextAppointment error ${response.code()}: $errorMsg")
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                    Log.e(tag, "Error de red en getNextAppointment", t)
                    callback(Result.failure(t))
                }
            })
    }

    fun getLevels(token: String, callback: (Result<List<Level>>) -> Unit) {
        Log.d(tag, "Llamando a getLevels")
        RetrofitClient.apiService.getLevels("Bearer $token")
            .enqueue(object : Callback<LevelsResponse> {
                override fun onResponse(
                    call: Call<LevelsResponse>, response: Response<LevelsResponse>
                ) {
                    if (response.isSuccessful) {
                        val levels = response.body()?.data?.map {
                            Level(
                                id = it.id,
                                name = it.name,
                                required = it.requiredXp,
                                reward = it.reward
                            )
                        } ?: emptyList()
                        Log.d(tag, "getLevels exitoso, ${levels.size} niveles")
                        callback(Result.success(levels))
                    } else {
                        val errorMsg = try {
                            response.errorBody()?.string() ?: "Error ${response.code()}"
                        } catch (_: Exception) {
                            "Error ${response.code()}"
                        }
                        Log.e(tag, "getLevels error ${response.code()}: $errorMsg")
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(call: Call<LevelsResponse>, t: Throwable) {
                    Log.e(tag, "Error de red en getLevels", t)
                    callback(Result.failure(t))
                }
            })
    }
}