package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.AppointmentResponse
import com.example.hairup.api.models.AppointmentsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentRepository {

    private val tag = "AppointmentRepository"

    fun getUserAppointments(token: String, callback: (Result<List<AppointmentResponse>>) -> Unit) {
        Log.d(tag, "Obteniendo todas las citas")

        RetrofitClient.apiService.getUserAppointments("Bearer $token")
            .enqueue(object : Callback<AppointmentsResponse> {
                override fun onResponse(
                    call: Call<AppointmentsResponse>, response: Response<AppointmentsResponse>
                ) {
                    if (response.isSuccessful) {
                        val appointments = response.body()?.data ?: emptyList()
                        callback(Result.success(appointments))
                    } else {
                        callback(Result.failure(Exception("Error ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<AppointmentsResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun cancelAppointment(
        token: String, appointmentId: Int, callback: (Result<Boolean>) -> Unit
    ) {
        RetrofitClient.apiService.deleteAppointment("Bearer $token", appointmentId)
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(
                    call: Call<Map<String, String>>, response: Response<Map<String, String>>
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(true))
                    } else {
                        val errorMsg = try {
                            response.errorBody()?.string() ?: "Error ${response.code()}"
                        } catch (_: Exception) {
                            "Error ${response.code()}"
                        }
                        callback(Result.failure(Exception(errorMsg)))
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }
}