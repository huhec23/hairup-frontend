package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.api.models.CreateCategoryRequest
import com.example.hairup.api.models.CreateProductRequest
import com.example.hairup.api.models.UpdateProductRequest
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.AdminProductRepository
import com.example.hairup.data.repository.ShopRepository
import com.example.hairup.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminProductViewModel(
    private val sessionManager: SessionManager,
    private val shopRepository: ShopRepository = ShopRepository(),
    private val adminRepository: AdminProductRepository = AdminProductRepository()
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _categories = MutableStateFlow<List<Pair<Int, String>>>(emptyList())
    val categories: StateFlow<List<Pair<Int, String>>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess: StateFlow<Boolean> = _operationSuccess

    init {
        loadProducts()
        loadCategories()
    }

    fun loadProducts() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true

        shopRepository.getProducts(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { productResponses ->
                    _products.value = productResponses.map { it.toProduct() }
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar productos"
                    _isLoading.value = false
                })
            }
        }
    }

    fun loadCategories() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) return

        shopRepository.getCategories(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { categoryResponses ->
                    _categories.value = categoryResponses.map { it.id to it.name }
                }, onFailure = {
                    _categories.value = emptyList()
                })
            }
        }
    }

    fun createCategory(name: String) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true

        val request = CreateCategoryRequest(name)


        shopRepository.createCategory(token, request) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    if (response.success) {
                        _successMessage.value = response.message
                        loadCategories()
                    } else {
                        _errorMessage.value = response.message
                    }
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al crear categoría"
                    _isLoading.value = false
                })
            }
        }
    }

    fun createProduct(
        name: String,
        description: String,
        price: Double,
        image: String,
        available: Boolean,
        points: Int,
        categoryId: Int?
    ) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false

        val request = CreateProductRequest(
            name = name,
            description = description.takeIf { it.isNotBlank() },
            price = price,
            image = image.takeIf { it.isNotBlank() },
            available = available,
            points = points,
            categoryId = categoryId
        )

        adminRepository.createProduct(token, request) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    _successMessage.value = response["message"] as? String ?: "Producto creado"
                    _operationSuccess.value = true
                    loadProducts()
                    loadCategories()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al crear producto"
                    _isLoading.value = false
                })
            }
        }
    }

    fun updateProduct(
        productId: Int,
        name: String,
        description: String,
        price: Double,
        image: String,
        available: Boolean,
        points: Int,
        categoryId: Int?
    ) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false

        val request = UpdateProductRequest(
            name = name.takeIf { it.isNotBlank() },
            description = description.takeIf { it.isNotBlank() },
            price = price,
            image = image.takeIf { it.isNotBlank() },
            available = available,
            points = points,
            categoryId = categoryId
        )

        adminRepository.updateProduct(token, productId, request) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    _successMessage.value = response["message"] as? String ?: "Producto actualizado"
                    _operationSuccess.value = true
                    loadProducts()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al actualizar producto"
                    _isLoading.value = false
                })
            }
        }
    }

    fun deleteProduct(productId: Int) {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _errorMessage.value = "No hay sesión activa"
            return
        }

        _isLoading.value = true
        _operationSuccess.value = false

        adminRepository.deleteProduct(token, productId) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    _successMessage.value = response["message"] as? String ?: "Producto eliminado"
                    _operationSuccess.value = true
                    loadProducts()
                }, onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al eliminar producto"
                    _isLoading.value = false
                })
            }
        }
    }

    fun toggleAvailability(product: Product) {
        updateProduct(
            productId = product.id,
            name = product.name,
            description = product.description,
            price = product.price,
            image = product.image,
            available = !product.available,
            points = product.points,
            categoryId = if (product.categoryId > 0) product.categoryId else null
        )
    }

    fun getCategoryName(categoryId: Int): String {
        return _categories.value.find { it.first == categoryId }?.second ?: ""
    }

    fun resetStates() {
        _errorMessage.value = null
        _successMessage.value = null
        _operationSuccess.value = false
        _isLoading.value = false
    }
}