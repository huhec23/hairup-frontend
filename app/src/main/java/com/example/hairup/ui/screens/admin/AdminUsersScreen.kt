package com.example.hairup.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShieldMoon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hairup.data.SessionManager
import com.example.hairup.model.AdminUser
import com.example.hairup.ui.viewmodel.AdminUserViewModel
import com.example.hairup.ui.viewmodel.AdminUserViewModelFactory

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val GreenConfirmed = Color(0xFF4CAF50)
private val RedCancel = Color(0xFFE53935)
private val LeatherBrown = Color(0xFF8B5E3C)
private val BlueAccent = Color(0xFF64B5F6)

private sealed class UserAction {
    data class ToggleAdmin(val user: AdminUser, val makeAdmin: Boolean) : UserAction()
    data class ToggleActive(val user: AdminUser, val active: Boolean) : UserAction()
}

@Composable
fun AdminUsersScreen() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: AdminUserViewModel = viewModel(
        factory = AdminUserViewModelFactory(sessionManager)
    )

    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }
    var pendingAction by remember { mutableStateOf<UserAction?>(null) }

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

    val filteredUsers = remember(searchQuery, selectedFilter, users) {
        viewModel.getFilteredUsers(searchQuery, selectedFilter)
    }

    val filterOptions = listOf(
        "Todos" to users.size,
        "Clientes" to users.count { !it.isAdmin },
        "Admins" to users.count { it.isAdmin },
        "Deshabilitados" to users.count { !it.isActive })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestión de Usuarios",
                style = MaterialTheme.typography.titleMedium,
                color = Gold
            )
            Text(
                text = "${users.size} registrados",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar por nombre o email...", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Gold) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                unfocusedBorderColor = LeatherBrown,
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = Gold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { (label, count) ->
                val isSelected = selectedFilter == label
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Gold else DarkGray)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selectedFilter = label }
                        .padding(horizontal = 14.dp, vertical = 7.dp)) {
                    Text(
                        text = "$label ($count)",
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CarbonBlack else TextGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading && users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Gold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredUsers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ManageAccounts,
                                contentDescription = null,
                                tint = TextGray.copy(alpha = 0.4f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No se encontraron usuarios con esa búsqueda"
                                else "No hay usuarios en esta categoría",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextGray
                            )
                        }
                    }
                } else {
                    filteredUsers.forEach { user ->
                        UserCard(user = user, onToggleAdmin = {
                            pendingAction = UserAction.ToggleAdmin(user, !user.isAdmin)
                        }, onToggleActive = {
                            pendingAction = UserAction.ToggleActive(user, !user.isActive)
                        })
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState, modifier = Modifier.padding(16.dp)
    ) { data ->
        Snackbar(
            containerColor = DarkGray, contentColor = White, snackbarData = data
        )
    }

    pendingAction?.let { action ->
        val title: String
        val message: String
        val confirmLabel: String
        val btnColor: Color

        when (action) {
            is UserAction.ToggleAdmin -> {
                title =
                    if (action.makeAdmin) "Dar privilegios de admin" else "Quitar privilegios de admin"
                message =
                    if (action.makeAdmin) "¿Dar privilegios de administrador a ${action.user.name}? Tendrá acceso completo al panel de admin."
                    else "¿Quitar los privilegios de administrador a ${action.user.name}? Pasará a ser un cliente normal."
                confirmLabel = if (action.makeAdmin) "Sí, dar admin" else "Sí, quitar admin"
                btnColor = if (action.makeAdmin) Gold else RedCancel
            }

            is UserAction.ToggleActive -> {
                title = if (action.active) "Habilitar usuario" else "Deshabilitar usuario"
                message =
                    if (action.active) "¿Habilitar la cuenta de ${action.user.name}? Recuperará el acceso a la app."
                    else "¿Deshabilitar la cuenta de ${action.user.name}? No podrá acceder a la app."
                confirmLabel = if (action.active) "Sí, habilitar" else "Sí, deshabilitar"
                btnColor = if (action.active) GreenConfirmed else RedCancel
            }
        }

        val btnTextColor = if (btnColor == Gold) CarbonBlack else White

        AlertDialog(
            onDismissRequest = { pendingAction = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = { Text(message) },
            confirmButton = {
                Button(
                    onClick = {
                        when (action) {
                            is UserAction.ToggleAdmin -> {
                                viewModel.toggleAdmin(action.user.id, action.makeAdmin)
                            }

                            is UserAction.ToggleActive -> {
                                viewModel.toggleActive(action.user.id, action.active)
                            }
                        }
                        pendingAction = null
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = btnColor, contentColor = btnTextColor
                    ), shape = RoundedCornerShape(8.dp)
                ) { Text(confirmLabel, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text("Cancelar", color = TextGray)
                }
            })
    }
}

@Composable
private fun UserCard(
    user: AdminUser, onToggleAdmin: () -> Unit, onToggleActive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (user.isActive) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(user.avatarColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.initials,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = user.avatarColor
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = user.nameColor
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RoleBadge(isAdmin = user.isAdmin)
                    StatusBadge(isActive = user.isActive)
                }
            }

            if (!user.isAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(user.levelColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = user.level,
                            style = MaterialTheme.typography.labelSmall,
                            color = user.levelColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = "${user.xp} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${user.totalBookings} citas",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = White.copy(alpha = 0.06f))
            Spacer(modifier = Modifier.height(12.dp))

            if (user.id == 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Gold.copy(alpha = 0.07f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cuenta de administrador principal — no modificable",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onToggleAdmin,
                        modifier = Modifier.weight(1f),
                        enabled = user.isActive,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.isAdmin) RedCancel.copy(alpha = 0.12f) else Gold.copy(
                                alpha = 0.15f
                            ),
                            contentColor = if (user.isAdmin) RedCancel else Gold,
                            disabledContainerColor = TextGray.copy(alpha = 0.1f),
                            disabledContentColor = TextGray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = if (user.isAdmin) Icons.Default.ShieldMoon else Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.isAdmin) "Quitar admin" else "Dar admin",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onToggleActive,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.isActive) RedCancel.copy(alpha = 0.12f) else GreenConfirmed.copy(
                                alpha = 0.15f
                            ), contentColor = if (user.isActive) RedCancel else GreenConfirmed
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            imageVector = if (user.isActive) Icons.Default.Block else Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.isActive) "Deshabilitar" else "Habilitar",
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(isAdmin: Boolean) {
    val color = if (isAdmin) Gold else BlueAccent
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = if (isAdmin) "Admin" else "Cliente",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun StatusBadge(isActive: Boolean) {
    val color = if (isActive) GreenConfirmed else RedCancel
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = if (isActive) "Activo" else "Deshabilitado",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}