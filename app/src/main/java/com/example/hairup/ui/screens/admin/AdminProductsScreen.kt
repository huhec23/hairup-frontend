package com.example.hairup.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hairup.data.SessionManager
import com.example.hairup.model.Product
import com.example.hairup.ui.viewmodel.AdminProductViewModel
import com.example.hairup.ui.viewmodel.AdminProductViewModelFactory

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val GreenConfirmed = Color(0xFF4CAF50)
private val RedCancel = Color(0xFFE53935)
private val LeatherBrown = Color(0xFF8B5E3C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: AdminProductViewModel = viewModel(
        factory = AdminProductViewModelFactory(sessionManager)
    )

    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetStates()
        }
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess) {
            successMessage?.let {
                snackbarHostState.showSnackbar(it)
            }
            viewModel.resetStates()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Inventario de Productos",
                style = MaterialTheme.typography.titleMedium,
                color = Gold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${products.count { it.available }} disponibles · ${products.count { !it.available }} no disponibles",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading && products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold)
                }
            } else {
                products.forEach { product ->
                    ProductCard(
                        product = product,
                        categoryName = viewModel.getCategoryName(product.categoryId),
                        onToggleAvailability = {
                            viewModel.toggleAvailability(product)
                        },
                        onEdit = {
                            editingProduct = product
                            showDialog = true
                        },
                        onDelete = {
                            productToDelete = product
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(72.dp))
        }

        FloatingActionButton(
            onClick = {
                editingProduct = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Gold,
            contentColor = CarbonBlack
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir producto")
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                containerColor = DarkGray,
                contentColor = White,
                snackbarData = data
            )
        }
    }

    if (showDialog) {
        ProductDialog(
            product = editingProduct,
            categories = categories,
            onAddCategory = { categoryName ->

                viewModel.createCategory(categoryName)
            },
            onDismiss = { showDialog = false; editingProduct = null },
            onSave = { name, description, price, imageUrl, available, points, categoryId ->
                if (editingProduct != null) {
                    viewModel.updateProduct(
                        productId = editingProduct!!.id,
                        name = name,
                        description = description,
                        price = price,
                        image = imageUrl,
                        available = available,
                        points = points,
                        categoryId = categoryId
                    )
                } else {
                    viewModel.createProduct(
                        name = name,
                        description = description,
                        price = price,
                        image = imageUrl,
                        available = available,
                        points = points,
                        categoryId = categoryId
                    )
                }
                showDialog = false
                editingProduct = null
            }
        )
    }

    if (showDeleteDialog && productToDelete != null) {
        val prod = productToDelete!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; productToDelete = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text("Eliminar producto", fontWeight = FontWeight.Bold) },
            text = { Text("¿Eliminar \"${prod.name}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(prod.id)
                        showDeleteDialog = false
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedCancel,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; productToDelete = null }) {
                    Text("Cancelar", color = Gold)
                }
            }
        )
    }
}

@Composable
private fun ProductCard(
    product: Product,
    categoryName: String,
    onToggleAvailability: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val availColor = if (product.available) GreenConfirmed else TextGray

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Gold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "€${product.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    if (categoryName.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(LeatherBrown.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.labelSmall,
                                color = LeatherBrown,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(availColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (product.available) "Disponible" else "No disponible",
                            style = MaterialTheme.typography.labelSmall,
                            color = availColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Gold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = RedCancel,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        ) {
            Button(
                onClick = onToggleAvailability,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (product.available)
                        LeatherBrown.copy(alpha = 0.2f)
                    else
                        GreenConfirmed.copy(alpha = 0.15f),
                    contentColor = if (product.available) LeatherBrown else GreenConfirmed
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (product.available) "Marcar como no disponible" else "Marcar como disponible",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialog(
    product: Product?,
    categories: List<Pair<Int, String>>,
    onAddCategory: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, price: Double, imageUrl: String, available: Boolean, points: Int, categoryId: Int?) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var priceText by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(product?.image ?: "") }
    var pointsText by remember { mutableStateOf(product?.points?.toString() ?: "0") }
    var available by remember { mutableStateOf(product?.available ?: true) }

    var selectedCategoryId by remember { mutableIntStateOf(product?.categoryId ?: -1) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showNewCategoryField by remember { mutableStateOf(false) }
    var newCategoryText by remember { mutableStateOf("") }
    var newCategoryError by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var pointsError by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Gold,
        unfocusedBorderColor = LeatherBrown,
        focusedLabelColor = Gold,
        unfocusedLabelColor = TextGray,
        cursorColor = Gold,
        focusedTextColor = White,
        unfocusedTextColor = White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkGray,
        titleContentColor = White,
        title = {
            Text(
                text = if (product != null) "Editar producto" else "Nuevo producto",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Nombre") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors,
                    supportingText = if (nameError) {
                        { Text("El nombre es obligatorio", color = RedCancel) }
                    } else null
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it; priceError = false },
                    label = { Text("Precio (€)") },
                    isError = priceError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors,
                    supportingText = if (priceError) {
                        { Text("Introduce un precio válido", color = RedCancel) }
                    } else null
                )

                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { pointsText = it; pointsError = false },
                    label = { Text("Puntos que da al comprar") },
                    isError = pointsError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors,
                    supportingText = if (pointsError) {
                        { Text("Introduce un número válido", color = RedCancel) }
                    } else null
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = categories.find { it.first == selectedCategoryId }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        placeholder = { Text("Seleccionar categoría", color = TextGray.copy(alpha = 0.5f)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = fieldColors
                    )

                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(DarkGray)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Sin categoría",
                                    color = if (selectedCategoryId == -1) Gold else TextGray,
                                    fontWeight = if (selectedCategoryId == -1) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedCategoryId = -1
                                categoryExpanded = false
                                showNewCategoryField = false
                            }
                        )

                        if (categories.isNotEmpty()) {
                            Divider(
                                color = LeatherBrown.copy(alpha = 0.3f),
                                thickness = 0.5.dp
                            )
                        }

                        categories.forEach { (id, name) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = name,
                                        color = if (selectedCategoryId == id) Gold else White,
                                        fontWeight = if (selectedCategoryId == id) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedCategoryId = id
                                    categoryExpanded = false
                                    showNewCategoryField = false
                                }
                            )
                        }

                        Divider(
                            color = LeatherBrown.copy(alpha = 0.3f),
                            thickness = 0.5.dp
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Gold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Nueva categoría",
                                        color = Gold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            },
                            onClick = {
                                categoryExpanded = false
                                showNewCategoryField = true
                                newCategoryText = ""
                                newCategoryError = false
                            }
                        )
                    }
                }

                if (showNewCategoryField) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newCategoryText,
                                onValueChange = { newCategoryText = it; newCategoryError = false },
                                label = { Text("Nueva categoría") },
                                isError = newCategoryError,
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = fieldColors
                            )
                            IconButton(
                                onClick = {
                                    val trimmed = newCategoryText.trim()
                                    when {
                                        trimmed.isBlank() -> newCategoryError = true
                                        categories.any { it.second.equals(trimmed, ignoreCase = true) } -> newCategoryError = true
                                        else -> {
                                            onAddCategory(trimmed)
                                            showNewCategoryField = false
                                            newCategoryText = ""
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Gold.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Confirmar",
                                    tint = Gold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        if (newCategoryError) {
                            Text(
                                text = if (newCategoryText.isBlank()) "El nombre no puede estar vacío"
                                else "Esta categoría ya existe",
                                style = MaterialTheme.typography.labelSmall,
                                color = RedCancel,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de imagen (opcional)") },
                    placeholder = { Text("https://...", color = TextGray.copy(alpha = 0.5f)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { available = !available }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (available) Gold else DarkGray)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (available) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = CarbonBlack,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Producto disponible",
                        color = White,
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = name.trim()
                    val price = priceText.toDoubleOrNull()
                    val points = pointsText.toIntOrNull()

                    nameError = trimmedName.isBlank()
                    priceError = price == null || price < 0
                    pointsError = points == null || points < 0

                    if (!nameError && !priceError && !pointsError) {
                        onSave(
                            trimmedName,
                            description.trim(),
                            price!!,
                            imageUrl.trim(),
                            available,
                            points!!,
                            if (selectedCategoryId > 0) selectedCategoryId else null
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = CarbonBlack
                ),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Guardar", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        }
    )
}