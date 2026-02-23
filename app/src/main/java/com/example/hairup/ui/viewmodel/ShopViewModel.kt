package com.example.hairup.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairup.api.models.PurchaseItem
import com.example.hairup.data.SessionManager
import com.example.hairup.data.repository.ShopRepository
import com.example.hairup.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShopViewModel(
    private val sessionManager: SessionManager,
    private val repository: ShopRepository = ShopRepository()
) : ViewModel() {

    private val _shopState = MutableStateFlow<ShopState>(ShopState.Loading)
    val shopState: StateFlow<ShopState> = _shopState

    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categories: StateFlow<List<CategoryItem>> = _categories

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _purchaseResult = MutableStateFlow<PurchaseResult?>(null)
    val purchaseResult: StateFlow<PurchaseResult?> = _purchaseResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val tag = "ShopViewModel"

    data class CategoryItem(
        val id: Int, val name: String
    )

    data class CartItem(
        val product: Product, var quantity: Int
    )

    data class PurchaseResult(
        val success: Boolean,
        val message: String,
        val xpEarned: Int,
        val pointsEarned: Int,
        val newXp: Int,
        val newPoints: Int
    )

    sealed class ShopState {
        object Loading : ShopState()
        data class Success(val products: List<Product>) : ShopState()
        data class Error(val message: String) : ShopState()
    }

    fun loadProducts() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _shopState.value = ShopState.Error("No hay sesión activa")
            return
        }

        _shopState.value = ShopState.Loading

        repository.getProducts(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { productResponses ->
                    val products = productResponses.map { it.toProduct() }
                    _shopState.value = ShopState.Success(products)
                }, onFailure = { exception ->
                    _shopState.value = ShopState.Error(exception.message ?: "Error desconocido")
                })
            }
        }
    }

    fun loadCategories() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            return
        }

        repository.getCategories(token) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { categoryResponses ->
                    val categories = categoryResponses.map {
                        CategoryItem(id = it.id, name = it.name)
                    }
                    _categories.value = listOf(CategoryItem(id = -1, name = "Todos")) + categories
                }, onFailure = { exception ->
                    Log.e(tag, "Error cargando categorías", exception)
                    _categories.value = listOf(CategoryItem(id = -1, name = "Todos"))
                })
            }
        }
    }

    fun purchase() {
        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            _purchaseResult.value = PurchaseResult(
                success = false,
                message = "No hay sesión activa",
                xpEarned = 0,
                pointsEarned = 0,
                newXp = 0,
                newPoints = 0
            )
            return
        }

        if (_cartItems.value.isEmpty()) {
            _purchaseResult.value = PurchaseResult(
                success = false,
                message = "El carrito está vacío",
                xpEarned = 0,
                pointsEarned = 0,
                newXp = 0,
                newPoints = 0
            )
            return
        }

        _isLoading.value = true
        _purchaseResult.value = null

        val purchaseItems = _cartItems.value.map {
            PurchaseItem(productId = it.product.id, quantity = it.quantity)
        }

        repository.purchaseProducts(token, purchaseItems) { result ->
            viewModelScope.launch {
                result.fold(onSuccess = { response ->
                    if (response.success) {
                        sessionManager.getUser()?.let { user ->
                            val updatedUser = user.copy(
                                xp = response.newXp, points = response.newPoints
                            )
                            sessionManager.saveAuthData(token, updatedUser)
                        }

                        _purchaseResult.value = PurchaseResult(
                            success = true,
                            message = response.message,
                            xpEarned = response.xpEarned,
                            pointsEarned = response.pointsEarned,
                            newXp = response.newXp,
                            newPoints = response.newPoints
                        )

                        _cartItems.value = emptyList()
                    } else {
                        _purchaseResult.value = PurchaseResult(
                            success = false,
                            message = response.message,
                            xpEarned = 0,
                            pointsEarned = 0,
                            newXp = 0,
                            newPoints = 0
                        )
                    }
                    _isLoading.value = false
                }, onFailure = { exception ->
                    _purchaseResult.value = PurchaseResult(
                        success = false,
                        message = exception.message ?: "Error al procesar compra",
                        xpEarned = 0,
                        pointsEarned = 0,
                        newXp = 0,
                        newPoints = 0
                    )
                    _isLoading.value = false
                })
            }
        }
    }

    fun resetPurchaseResult() {
        _purchaseResult.value = null
    }

    fun addToCart(product: Product) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == product.id }

        if (existingItem != null) {
            val index = currentCart.indexOf(existingItem)
            currentCart[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentCart.add(CartItem(product = product, quantity = 1))
        }

        _cartItems.value = currentCart
    }

    fun removeFromCart(productId: Int) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == productId } ?: return

        if (existingItem.quantity > 1) {
            val index = currentCart.indexOf(existingItem)
            currentCart[index] = existingItem.copy(quantity = existingItem.quantity - 1)
        } else {
            currentCart.remove(existingItem)
        }

        _cartItems.value = currentCart
    }

    fun deleteFromCart(productId: Int) {
        val currentCart = _cartItems.value.toMutableList()
        currentCart.removeAll { it.product.id == productId }
        _cartItems.value = currentCart
    }

    fun getCartItems(): List<CartItem> = _cartItems.value
}