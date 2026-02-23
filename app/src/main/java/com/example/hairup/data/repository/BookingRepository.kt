package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.AppointmentResponse
import com.example.hairup.api.models.AvailabilityResponse
import com.example.hairup.api.models.BarberResponse
import com.example.hairup.api.models.BarbersResponse
import com.example.hairup.api.models.CreateAppointmentRequest
import com.example.hairup.api.models.ServiceResponse
import com.example.hairup.api.models.ServicesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingRepository {

    private val tag = "BookingRepository"

    fun getServices(token: String, callback: (Result<List<ServiceResponse>>) -> Unit) {
        Log.d(tag, "Obteniendo servicios")

        RetrofitClient.apiService.getServices("Bearer $token")
            .enqueue(object : Callback<ServicesResponse> {
                override fun onResponse(
                    call: Call<ServicesResponse>, response: Response<ServicesResponse>
                ) {
                    if (response.isSuccessful) {
                        val services = response.body()?.data ?: emptyList()
                        callback(Result.success(services))
                    } else {
                        callback(Result.failure(Exception("Error ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<ServicesResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun getBarbers(token: String, callback: (Result<List<BarberResponse>>) -> Unit) {
        Log.d(tag, "Obteniendo barberos")

        RetrofitClient.apiService.getBarbers("Bearer $token")
            .enqueue(object : Callback<BarbersResponse> {
                override fun onResponse(
                    call: Call<BarbersResponse>, response: Response<BarbersResponse>
                ) {
                    if (response.isSuccessful) {
                        val barbers = response.body()?.data ?: emptyList()
                        callback(Result.success(barbers))
                    } else {
                        callback(Result.failure(Exception("Error ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<BarbersResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun getBarberAvailability(
        token: String, barberId: Int, date: String, callback: (Result<AvailabilityResponse>) -> Unit
    ) {
        Log.d(tag, "Obteniendo disponibilidad para barbero $barberId en fecha $date")

        RetrofitClient.apiService.getBarberAvailability("Bearer $token", barberId, date)
            .enqueue(object : Callback<AvailabilityResponse> {
                override fun onResponse(
                    call: Call<AvailabilityResponse>, response: Response<AvailabilityResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { callback(Result.success(it)) }
                            ?: callback(Result.failure(Exception("Respuesta vacía")))
                    } else {
                        callback(Result.failure(Exception("Error ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<AvailabilityResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun createBooking(
        token: String,
        serviceId: Int,
        date: String,
        time: String,
        barberId: Int,
        callback: (Result<Int>) -> Unit
    ) {
        Log.d(tag, "Creando reserva")

        val request = CreateAppointmentRequest(serviceId, date, time, barberId)

        RetrofitClient.apiService.createAppointment("Bearer $token", request)
            .enqueue(object : Callback<AppointmentResponse> {
                override fun onResponse(
                    call: Call<AppointmentResponse>, response: Response<AppointmentResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback(Result.success(it.id))
                        } ?: callback(Result.failure(Exception("Respuesta vacía")))
                    } else {
                        callback(Result.failure(Exception("Error ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<AppointmentResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }
}