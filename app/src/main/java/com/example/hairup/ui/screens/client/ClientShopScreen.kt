package com.example.hairup.ui.screens.client

import androidx.compose.foundation.Image
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hairup.R
import com.example.hairup.data.SessionManager
import com.example.hairup.model.Product
import com.example.hairup.ui.viewmodel.ShopViewModel
import com.example.hairup.ui.viewmodel.ShopViewModelFactory
import kotlinx.coroutines.launch

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val LeatherBrown = Color(0xFF8B5E3C)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: ShopViewModel = viewModel(
        factory = ShopViewModelFactory(sessionManager)
    )

    val shopState by viewModel.shopState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val purchaseResult by viewModel.purchaseResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableIntStateOf(-1) } // -1 = "Todos"
    var showCart by remember { mutableStateOf(false) }
    var showPurchaseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
        viewModel.loadCategories()
    }

    LaunchedEffect(purchaseResult) {
        if (purchaseResult != null) {
            showPurchaseDialog = true
        }
    }

    val filteredProducts = when (val state = shopState) {
        is ShopViewModel.ShopState.Success -> {
            state.products.filter { product ->
                val matchesCategory =
                    selectedCategoryId == -1 || product.categoryId == selectedCategoryId
                val matchesSearch = searchQuery.isBlank() || product.name.contains(
                    searchQuery,
                    ignoreCase = true
                ) || product.description.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
        }

        else -> emptyList()
    }

    val totalCartItems = cartItems.sumOf { it.quantity }

    fun getCartQuantity(productId: Int): Int {
        return cartItems.find { it.product.id == productId }?.quantity ?: 0
    }

    Scaffold(
        containerColor = CarbonBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(CarbonBlack)
        ) {
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
                                    containerColor = Gold, contentColor = CarbonBlack
                                ) {
                                    Text(
                                        text = "$totalCartItems",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito",
                            tint = Gold,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

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
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Gold,
                    unfocusedIndicatorColor = LeatherBrown.copy(alpha = 0.4f),
                    cursorColor = Gold,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedContainerColor = DarkGray,
                    unfocusedContainerColor = DarkGray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (categories.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategoryId == category.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) Gold else DarkGray
                                )
                                .clickable { selectedCategoryId = category.id }
                                .padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text(
                                text = category.name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CarbonBlack else TextGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (val state = shopState) {
                is ShopViewModel.ShopState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Gold)
                    }
                }

                is ShopViewModel.ShopState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error al cargar productos", color = TextGray, fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = Color(0xFFE53935),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadProducts() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gold, contentColor = CarbonBlack
                            )
                        ) {
                            Text("Reintentar")
                        }
                    }
                }

                is ShopViewModel.ShopState.Success -> {
                    if (filteredProducts.isEmpty()) {
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
                            items(filteredProducts, key = { it.id }) { product ->
                                ProductCard(
                                    product = product,
                                    cartQuantity = getCartQuantity(product.id),
                                    onAdd = { viewModel.addToCart(product) },
                                    onRemove = { viewModel.removeFromCart(product.id) })
                            }
                        }
                    }
                }
            }
        }
    }

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
                cartItems = cartItems,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { showCart = false }
                },
                onAdd = { viewModel.addToCart(it) },
                onRemove = { viewModel.removeFromCart(it.id) },
                onDelete = { viewModel.deleteFromCart(it.id) },
                onOrder = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showCart = false
                        viewModel.purchase()
                    }
                },
                isLoading = isLoading
            )
        }
    }

    if (showPurchaseDialog && purchaseResult != null) {
        AlertDialog(onDismissRequest = {
            showPurchaseDialog = false
            viewModel.resetPurchaseResult()
        }, containerColor = DarkGray, titleContentColor = White, icon = {
            if (purchaseResult!!.success) {
                Icon(
                    Icons.Default.Star, null, tint = Gold, modifier = Modifier.size(40.dp)
                )
            }
        }, title = {
            Text(
                text = if (purchaseResult!!.success) "¡Compra realizada!" else "Error",
                fontWeight = FontWeight.Bold,
                color = if (purchaseResult!!.success) Gold else Color(0xFFE53935)
            )
        }, text = {
            Column {
                if (purchaseResult!!.success) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Has ganado:", fontWeight = FontWeight.Bold, color = White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${purchaseResult!!.xpEarned} XP", color = Gold
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${purchaseResult!!.pointsEarned} puntos", color = GoldLight
                        )
                    }
                }
            }
        }, confirmButton = {
            Button(
                onClick = {
                    showPurchaseDialog = false
                    viewModel.resetPurchaseResult()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Gold, contentColor = CarbonBlack
                ), shape = RoundedCornerShape(8.dp)
            ) {
                Text("Aceptar")
            }
        })
    }
}

@Composable
private fun ProductCard(
    product: Product, cartQuantity: Int, onAdd: () -> Unit, onRemove: () -> Unit
) {
    val isAvailable = product.available

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(DarkGray),
                contentAlignment = Alignment.Center
            ) {
                if (product.image.isNotEmpty() && !product.image.startsWith("product_")) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(product.image)
                            .crossfade(true).build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                        alpha = if (isAvailable) 1f else 0.4f
                    )
                } else {
                    val imageRes = R.drawable.product_champu_reparador
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                        alpha = if (isAvailable) 1f else 0.4f
                    )
                }

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
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                                    .clickable { onRemove() }, contentAlignment = Alignment.Center
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
                                    .clickable { onAdd() }, contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Añadir",
                                    tint = Gold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else if (isAvailable) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Gold)
                                .clickable { onAdd() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir al carrito",
                                tint = CarbonBlack,
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
    cartItems: List<ShopViewModel.CartItem>,
    onClose: () -> Unit,
    onAdd: (Product) -> Unit,
    onRemove: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    onOrder: () -> Unit,
    isLoading: Boolean
) {
    val total = cartItems.sumOf { it.product.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp)
    ) {
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
                        onDelete = { onDelete(item.product) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = LeatherBrown.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total", style = MaterialTheme.typography.titleMedium, color = TextGray
                )
                Text(
                    text = formatPrice(total),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = CarbonBlack,
                    disabledContainerColor = LeatherBrown.copy(alpha = 0.3f),
                    disabledContentColor = TextGray.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = CarbonBlack, modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PROCESANDO...")
                } else {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Realizar Pedido", fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: ShopViewModel.CartItem, onAdd: () -> Unit, onRemove: () -> Unit, onDelete: () -> Unit
) {
    val subtotal = item.product.price * item.quantity

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Text(
                text = formatPrice(subtotal),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Gold,
                modifier = Modifier.width(56.dp),
                textAlign = TextAlign.End
            )

            IconButton(
                onClick = onDelete, modifier = Modifier.size(32.dp)
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