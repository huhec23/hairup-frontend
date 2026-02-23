package com.example.hairup.model

data class Product(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val image: String = "",
    val available: Boolean = true,
    val points: Int = 0,
    val categoryId: Int = 0,
    val categoryName: String = ""
)