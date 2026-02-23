package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.CreateProductRequest
import com.example.hairup.api.models.UpdateProductRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProductRepository {

    private val tag = "AdminProductRepository"

    fun createProduct(
        token: String, request: CreateProductRequest, callback: (Result<Map<String, Any>>) -> Unit
    ) {
        Log.d(tag, "Creando producto: ${request.name}")

        RetrofitClient.apiService.createProduct("Bearer $token", request)
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

    fun updateProduct(
        token: String,
        productId: Int,
        request: UpdateProductRequest,
        callback: (Result<Map<String, Any>>) -> Unit
    ) {
        Log.d(tag, "Actualizando producto $productId")

        RetrofitClient.apiService.updateProduct("Bearer $token", productId, request)
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

    fun deleteProduct(
        token: String, productId: Int, callback: (Result<Map<String, Any>>) -> Unit
    ) {
        Log.d(tag, "Eliminando producto $productId")

        RetrofitClient.apiService.deleteProduct("Bearer $token", productId)
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