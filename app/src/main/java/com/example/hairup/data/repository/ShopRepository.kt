package com.example.hairup.data.repository

import android.util.Log
import com.example.hairup.api.RetrofitClient
import com.example.hairup.api.models.AddPointsRequest
import com.example.hairup.api.models.AddPointsResponse
import com.example.hairup.api.models.CategoriesResponse
import com.example.hairup.api.models.CategoryResponse
import com.example.hairup.api.models.CategorySuccessResponse
import com.example.hairup.api.models.CreateCategoryRequest
import com.example.hairup.api.models.ProductResponse
import com.example.hairup.api.models.ProductsResponse
import com.example.hairup.api.models.PurchaseItem
import com.example.hairup.api.models.PurchaseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopRepository {

    private val tag = "ShopRepository"

    fun getProducts(token: String, callback: (Result<List<ProductResponse>>) -> Unit) {
        Log.d(tag, "Obteniendo productos del backend")

        RetrofitClient.apiService.getProducts("Bearer $token")
            .enqueue(object : Callback<ProductsResponse> {
                override fun onResponse(
                    call: Call<ProductsResponse>, response: Response<ProductsResponse>
                ) {
                    if (response.isSuccessful) {
                        val products = response.body()?.data ?: emptyList()
                        callback(Result.success(products))
                    } else {
                        callback(Result.failure(Exception("Error ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<ProductsResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun getCategories(token: String, callback: (Result<List<CategoryResponse>>) -> Unit) {
        Log.d(tag, "Obteniendo categorías")

        RetrofitClient.apiService.getCategories("Bearer $token")
            .enqueue(object : Callback<CategoriesResponse> {
                override fun onResponse(
                    call: Call<CategoriesResponse>, response: Response<CategoriesResponse>
                ) {
                    if (response.isSuccessful) {
                        val categories = response.body()?.data ?: emptyList()
                        callback(Result.success(categories))
                    } else {
                        callback(Result.failure(Exception("Error ${response.code()}")))
                    }
                }

                override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun createCategory(
        token: String,
        request: CreateCategoryRequest,
        callback: (Result<CategorySuccessResponse>) -> Unit
    ) {
        Log.d(tag, "Creando categoría: ${request.name}")

        RetrofitClient.apiService.createCategory("Bearer $token", request)
            .enqueue(object : Callback<CategorySuccessResponse> {
                override fun onResponse(
                    call: Call<CategorySuccessResponse>, response: Response<CategorySuccessResponse>
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

                override fun onFailure(call: Call<CategorySuccessResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    fun purchaseProducts(
        token: String, items: List<PurchaseItem>, callback: (Result<PurchaseResponse>) -> Unit
    ) {
        Log.d(tag, "Realizando compra de ${items.size} productos")

        var totalPoints: Int

        getProducts(token) { productsResult ->
            productsResult.fold(onSuccess = { products ->
                totalPoints = items.sumOf { item ->
                    val product = products.find { it.id == item.productId }
                    (product?.points ?: 0) * item.quantity
                }

                val request = AddPointsRequest(totalPoints)
                RetrofitClient.apiService.addPoints("Bearer $token", request)
                    .enqueue(object : Callback<AddPointsResponse> {
                        override fun onResponse(
                            call: Call<AddPointsResponse>, response: Response<AddPointsResponse>
                        ) {
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    val purchaseResponse = PurchaseResponse(
                                        success = body.success,
                                        message = body.message,
                                        xpEarned = body.xpEarned,
                                        pointsEarned = body.pointsEarned,
                                        newXp = body.newXp,
                                        newPoints = body.newPoints
                                    )
                                    callback(Result.success(purchaseResponse))
                                } else {
                                    callback(Result.failure(Exception("Respuesta vacía")))
                                }
                            } else {
                                callback(Result.failure(Exception("Error ${response.code()}")))
                            }
                        }

                        override fun onFailure(call: Call<AddPointsResponse>, t: Throwable) {
                            callback(Result.failure(t))
                        }
                    })
            }, onFailure = { exception ->
                callback(Result.failure(exception))
            })
        }
    }
}