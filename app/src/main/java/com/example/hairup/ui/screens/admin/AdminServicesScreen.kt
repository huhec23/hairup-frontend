package com.example.hairup.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hairup.data.SessionManager
import com.example.hairup.model.Service
import com.example.hairup.ui.viewmodel.AdminServiceViewModel
import com.example.hairup.ui.viewmodel.AdminServiceViewModelFactory

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val GoldDark = Color(0xFFA68829)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val RedCancel = Color(0xFFE53935)
private val LeatherBrown = Color(0xFF8B5E3C)
private val BlueAccent = Color(0xFF64B5F6)

@Composable
fun AdminServicesScreen() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: AdminServiceViewModel = viewModel(
        factory = AdminServiceViewModelFactory(sessionManager)
    )

    val services by viewModel.services.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingService by remember { mutableStateOf<Service?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var serviceToDelete by remember { mutableStateOf<Service?>(null) }

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
                text = "Servicios del Salón",
                style = MaterialTheme.typography.titleMedium,
                color = Gold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${services.size} servicios disponibles",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading && services.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold)
                }
            } else {
                services.forEach { service ->
                    ServiceCard(service = service, onEdit = {
                        editingService = service
                        showDialog = true
                    }, onDelete = {
                        serviceToDelete = service
                        showDeleteDialog = true
                    })
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(72.dp))
        }

        FloatingActionButton(
            onClick = {
                editingService = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Gold,
            contentColor = CarbonBlack
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir servicio")
        }

        SnackbarHost(
            hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                containerColor = DarkGray, contentColor = White, snackbarData = data
            )
        }
    }

    if (showDialog) {
        ServiceDialog(
            service = editingService,
            onDismiss = { showDialog = false; editingService = null },
            onSave = { name, description, price, duration, xp ->
                if (editingService != null) {
                    viewModel.updateService(
                        serviceId = editingService!!.id,
                        name = name,
                        description = description,
                        price = price,
                        duration = duration,
                        xp = xp
                    )
                } else {
                    viewModel.createService(
                        name = name,
                        description = description,
                        price = price,
                        duration = duration,
                        xp = xp
                    )
                }
                showDialog = false
                editingService = null
            })
    }

    if (showDeleteDialog && serviceToDelete != null) {
        val svc = serviceToDelete!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; serviceToDelete = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text("Eliminar servicio", fontWeight = FontWeight.Bold) },
            text = { Text("¿Eliminar \"${svc.name}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteService(svc.id)
                        showDeleteDialog = false
                        serviceToDelete = null
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = RedCancel, contentColor = White
                    ), shape = RoundedCornerShape(8.dp)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; serviceToDelete = null }) {
                    Text("Cancelar", color = Gold)
                }
            })
    }
}

@Composable
private fun ServiceCard(
    service: Service, onEdit: () -> Unit, onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(listOf(GoldDark, Gold, GoldLight))
                )
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Gold.copy(alpha = 0.15f)), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = service.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        maxLines = 1
                    )
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

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    modifier = Modifier.weight(1f),
                    label = "Precio",
                    value = "€${service.price.toInt()}",
                    valueColor = Gold
                )
                InfoChip(
                    modifier = Modifier.weight(1f),
                    label = "Duración",
                    value = "${service.duration} min",
                    valueColor = BlueAccent,
                    icon = {
                        Icon(
                            Icons.Default.Timer,
                            null,
                            tint = BlueAccent,
                            modifier = Modifier.size(13.dp)
                        )
                    })
                InfoChip(
                    modifier = Modifier.weight(1f),
                    label = "XP",
                    value = "+${service.xp} XP",
                    valueColor = GoldLight,
                    icon = {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = GoldLight,
                            modifier = Modifier.size(13.dp)
                        )
                    })
            }
        }
    }
}

@Composable
private fun InfoChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color,
    icon: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(White.copy(alpha = 0.04f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextGray,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.invoke()
            if (icon != null) Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ServiceDialog(
    service: Service?,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, price: Double, duration: Int, xp: Int) -> Unit
) {
    var name by remember { mutableStateOf(service?.name ?: "") }
    var description by remember { mutableStateOf(service?.description ?: "") }
    var priceText by remember { mutableStateOf(service?.price?.toString() ?: "") }
    var durationText by remember { mutableStateOf(service?.duration?.toString() ?: "") }
    var xpText by remember { mutableStateOf(service?.xp?.toString() ?: "0") }

    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var durationError by remember { mutableStateOf(false) }

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
                text = if (service != null) "Editar servicio" else "Nuevo servicio",
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
                    label = { Text("Nombre del servicio") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors,
                    supportingText = if (nameError) {
                        { Text("El nombre es obligatorio", color = RedCancel) }
                    } else null)

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it; priceError = false },
                        label = { Text("Precio (€)") },
                        isError = priceError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = fieldColors,
                        supportingText = if (priceError) {
                            { Text("Inválido", color = RedCancel) }
                        } else null)
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it; durationError = false },
                        label = { Text("Duración (min)") },
                        isError = durationError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = fieldColors,
                        supportingText = if (durationError) {
                            { Text("Inválido", color = RedCancel) }
                        } else null)
                }

                OutlinedTextField(
                    value = xpText,
                    onValueChange = { xpText = it },
                    label = { Text("XP que gana el cliente") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors,
                    leadingIcon = {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = GoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                    })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = name.trim()
                    val price = priceText.toDoubleOrNull()
                    val duration = durationText.toIntOrNull()
                    val xp = xpText.toIntOrNull() ?: 0

                    nameError = trimmedName.isBlank()
                    priceError = price == null || price < 0
                    durationError = duration == null || duration <= 0

                    if (!nameError && !priceError && !durationError) {
                        onSave(
                            trimmedName, description.trim(), price!!, duration!!, xp
                        )
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Gold, contentColor = CarbonBlack
                ), shape = RoundedCornerShape(8.dp)
            ) { Text("Guardar", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        })
}