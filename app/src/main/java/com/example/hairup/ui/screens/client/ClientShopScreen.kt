package com.example.hairup.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hairup.R
import com.example.hairup.model.Product
import kotlinx.coroutines.launch

// Theme colors
private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val GoldDark = Color(0xFFA68829)
private val LeatherBrown = Color(0xFF8B5E3C)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)

// Wrapper for Product with category
private data class ShopProduct(
    val product: Product,
    val category: String,
    val imageRes: Int
)

// Cart item
private data class CartItem(
    val product: Product,
    var quantity: Int
)

private val categories = listOf("Todos", "Champús", "Acondicionadores", "Tratamientos", "Styling", "Accesorios")

private val mockProducts = listOf(
    ShopProduct(
        Product(id = 1, name = "Champú Reparador", description = "Reparación intensiva para cabello dañado", price = 12.50, available = true),
        "Champús", R.drawable.product_champu_reparador
    ),
    ShopProduct(
        Product(id = 2, name = "Champú Volumen", description = "Da cuerpo y volumen al cabello fino", price = 10.90, available = true),
        "Champús", R.drawable.product_champu_volumen
    ),
    ShopProduct(
        Product(id = 3, name = "Acondicionador Hidratante", description = "Suavidad y brillo duradero", price = 11.00, available = true),
        "Acondicionadores", R.drawable.product_acondicionador
    ),
    ShopProduct(
        Product(id = 4, name = "Acondicionador Sin Aclarado", description = "Protección y nutrición todo el día", price = 13.50, available = false),
        "Acondicionadores", R.drawable.product_acondicionador_sin_aclarado
    ),
    ShopProduct(
        Product(id = 5, name = "Mascarilla Capilar", description = "Hidratación profunda semanal", price = 18.90, available = true),
        "Tratamientos", R.drawable.product_mascarilla
    ),
    ShopProduct(
        Product(id = 6, name = "Aceite de Argán", description = "Nutrición natural para puntas", price = 9.50, available = true),
        "Tratamientos", R.drawable.product_aceite_argan
    ),
    ShopProduct(
        Product(id = 7, name = "Sérum Reparador", description = "Tratamiento nocturno intensivo", price = 22.00, available = true),
        "Tratamientos", R.drawable.product_serum
    ),
    ShopProduct(
        Product(id = 8, name = "Spray Fijador", description = "Fijación fuerte sin residuos", price = 8.90, available = true),
        "Styling", R.drawable.product_spray
    ),
    ShopProduct(
        Product(id = 9, name = "Cera Mate", description = "Acabado natural con control", price = 11.50, available = true),
        "Styling", R.drawable.product_cera
    ),
    ShopProduct(
        Product(id = 10, name = "Cepillo Desenredante", description = "Suave con el cabello mojado", price = 7.90, available = true),
        "Accesorios", R.drawable.product_cepillo
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    val cart = remember { mutableStateListOf<CartItem>() }
    var showCart by remember { mutableStateOf(false) }
    var showOrderSuccess by remember { mutableStateOf(false) }
    var lastOrderXp by remember { mutableStateOf(0) }

    val totalCartItems = cart.sumOf { it.quantity }

    // Filter products
    val filteredProducts = mockProducts.filter { sp ->
        val matchesCategory = selectedCategory == "Todos" || sp.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() || sp.product.name.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    // Cart helper functions
    fun getCartQuantity(productId: Int): Int {
        return cart.find { it.product.id == productId }?.quantity ?: 0
    }

    fun addToCart(product: Product) {
        val existing = cart.find { it.product.id == product.id }
        if (existing != null) {
            val index = cart.indexOf(existing)
            cart[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            cart.add(CartItem(product = product, quantity = 1))
        }
    }

    fun removeFromCart(productId: Int) {
        val existing = cart.find { it.product.id == productId } ?: return
        if (existing.quantity > 1) {
            val index = cart.indexOf(existing)
            cart[index] = existing.copy(quantity = existing.quantity - 1)
        } else {
            cart.remove(existing)
        }
    }

    fun deleteFromCart(productId: Int) {
        cart.removeAll { it.product.id == productId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
    ) {
        // ===== A) Top Bar =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tienda",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Gold
            )

            IconButton(onClick = { showCart = true }) {
                BadgedBox(
                    badge = {
                        if (totalCartItems > 0) {
                            Badge(
                                containerColor = Gold,
                                contentColor = CarbonBlack
                            ) {
                                Text(
                                    text = "$totalCartItems",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Carrito",
                        tint = Gold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // ===== B) Search Bar =====
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar productos...", color = TextGray.copy(alpha = 0.6f)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Gold,
                unfocusedBorderColor = LeatherBrown.copy(alpha = 0.4f),
                cursorColor = Gold,
                focusedTextColor = White,
                unfocusedTextColor = White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ===== C) Category Filters =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) Gold else DarkGray
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CarbonBlack else TextGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ===== D) Product Grid =====
        if (filteredProducts.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextGray.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No se encontraron productos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextGray
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts, key = { it.product.id }) { shopProduct ->
                    ProductCard(
                        shopProduct = shopProduct,
                        cartQuantity = getCartQuantity(shopProduct.product.id),
                        onAdd = { addToCart(shopProduct.product) },
                        onRemove = { removeFromCart(shopProduct.product.id) }
                    )
                }
            }
        }
    }

    // ===== E) Cart Bottom Sheet =====
    if (showCart) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        ModalBottomSheet(
            onDismissRequest = { showCart = false },
            sheetState = sheetState,
            containerColor = DarkGray,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            CartContent(
                cartItems = cart.toList(),
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { showCart = false }
                },
                onAdd = { addToCart(it) },
                onRemove = { removeFromCart(it.id) },
                onDelete = { deleteFromCart(it.id) },
                onOrder = {
                    val xp = cart.sumOf { (it.product.price * 2).toInt() * it.quantity }
                    lastOrderXp = xp
                    cart.clear()
                    showCart = false
                    showOrderSuccess = true
                }
            )
        }
    }

    // Order success dialog
    if (showOrderSuccess) {
        AlertDialog(
            onDismissRequest = { showOrderSuccess = false },
            containerColor = DarkGray,
            titleContentColor = White,
            title = {
                Text(
                    text = "Pedido realizado",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Tu pedido ha sido procesado correctamente.",
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Has ganado $lastOrderXp XP",
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showOrderSuccess = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = CarbonBlack
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun ProductCard(
    shopProduct: ShopProduct,
    cartQuantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    val product = shopProduct.product
    val isAvailable = product.available

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Product image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(DarkGray),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = shopProduct.imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                    alpha = if (isAvailable) 1f else 0.4f
                )

                // Sold out overlay
                if (!isAvailable) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CarbonBlack.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Agotado",
                            fontWeight = FontWeight.Bold,
                            color = White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                // Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Description
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price + Cart controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatPrice(product.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold,
                        fontSize = 15.sp
                    )

                    if (cartQuantity > 0 && isAvailable) {
                        // Quantity controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Gold.copy(alpha = 0.15f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                                    .clickable { onRemove() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Quitar",
                                    tint = Gold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "$cartQuantity",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gold,
                                modifier = Modifier.width(24.dp),
                                textAlign = TextAlign.Center
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                                    .clickable { onAdd() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Añadir",
                                    tint = Gold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        // Add button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isAvailable) Gold else LeatherBrown.copy(alpha = 0.3f)
                                )
                                .then(
                                    if (isAvailable) Modifier.clickable { onAdd() } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir al carrito",
                                tint = if (isAvailable) CarbonBlack else TextGray.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartContent(
    cartItems: List<CartItem>,
    onClose: () -> Unit,
    onAdd: (Product) -> Unit,
    onRemove: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    onOrder: () -> Unit
) {
    val total = cartItems.sumOf { it.product.price * it.quantity }
    val totalXp = cartItems.sumOf { (it.product.price * 2).toInt() * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mi Carrito",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = White
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (cartItems.isEmpty()) {
            // Empty cart
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = TextGray.copy(alpha = 0.4f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tu carrito está vacío",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextGray
                )
            }
        } else {
            // Cart items
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                cartItems.forEach { item ->
                    CartItemRow(
                        item = item,
                        onAdd = { onAdd(item.product) },
                        onRemove = { onRemove(item.product) },
                        onDelete = { onDelete(item.product) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = LeatherBrown.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextGray
                )
                Text(
                    text = formatPrice(total),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // XP to earn
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Gold.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Ganarás $totalXp XP con esta compra",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldLight.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Order button
            Button(
                onClick = onOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = CarbonBlack
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Realizar Pedido",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDelete: () -> Unit
) {
    val subtotal = item.product.price * item.quantity

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${formatPrice(item.product.price)} / ud.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quantity controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Gold.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Quitar",
                        tint = Gold,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "${item.quantity}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gold,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onAdd() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir",
                        tint = Gold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Subtotal
            Text(
                text = formatPrice(subtotal),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Gold,
                modifier = Modifier.width(56.dp),
                textAlign = TextAlign.End
            )

            // Delete
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFE53935).copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return if (price == price.toLong().toDouble()) {
        "${price.toLong()}€"
    } else {
        String.format("%.2f€", price)
    }
}
