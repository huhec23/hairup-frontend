package com.example.hairup.ui.screens.client

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hairup.data.SessionManager
import com.example.hairup.ui.components.AppTextInput
import com.example.hairup.ui.viewmodel.ProfileViewModel
import com.example.hairup.ui.viewmodel.ProfileViewModelFactory

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val GoldDark = Color(0xFFA68829)
private val LeatherBrown = Color(0xFF8B5E3C)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val RedLogout = Color(0xFFE53935)

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {}, onProfileUpdated: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(sessionManager)
    )

    val profileState by viewModel.profileState.collectAsState()
    val appointmentsCount by viewModel.appointmentsCount.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val passwordState by viewModel.passwordState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }


    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var initialName by remember { mutableStateOf("") }
    var initialPhone by remember { mutableStateOf("") }

    var notifAppointments by remember { mutableStateOf(true) }
    var notifOffers by remember { mutableStateOf(true) }
    var notifReminders by remember { mutableStateOf(true) }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(profileState) {
        when (profileState) {
            is ProfileViewModel.ProfileState.Success -> {
                val state = profileState as ProfileViewModel.ProfileState.Success
                name = state.user.name
                email = state.user.email
                phone = state.user.phone
                initialName = state.user.name
                initialPhone = state.user.phone
            }

            is ProfileViewModel.ProfileState.Error -> {
                val state = profileState as ProfileViewModel.ProfileState.Error
                snackbarHostState.showSnackbar(state.message)
            }

            else -> {}
        }
    }

    LaunchedEffect(updateState) {
        when (updateState) {
            is ProfileViewModel.UpdateState.Success -> {
                snackbarHostState.showSnackbar("Perfil actualizado correctamente")
                viewModel.resetUpdateState()
            }

            is ProfileViewModel.UpdateState.Error -> {
                val state = updateState as ProfileViewModel.UpdateState.Error
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetUpdateState()
            }

            else -> {}
        }
    }

    LaunchedEffect(passwordState) {
        when (passwordState) {
            is ProfileViewModel.PasswordState.Success -> {
                snackbarHostState.showSnackbar("Contraseña actualizada correctamente")
                viewModel.resetPasswordState()
                showPasswordDialog = false
            }

            is ProfileViewModel.PasswordState.Error -> {
                val state = passwordState as ProfileViewModel.PasswordState.Error
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetPasswordState()
            }

            else -> {}
        }
    }

    val hasChanges = name != initialName || phone != initialPhone

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        when (val state = profileState) {
            is ProfileViewModel.ProfileState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold)
                }
            }

            is ProfileViewModel.ProfileState.Success -> {
                val user = state.user

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(GoldDark, Gold, GoldLight)
                                )
                            ), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.firstOrNull()?.toString() ?: "U",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = CarbonBlack
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Gold.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Gold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Nivel ${user.levelId}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkGray),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(
                            icon = Icons.Default.CalendarMonth,
                            value = "$appointmentsCount",
                            label = "Citas"
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(LeatherBrown.copy(alpha = 0.3f))
                        )
                        StatItem(
                            icon = Icons.Default.Star, value = "${user.xp}", label = "XP"
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(LeatherBrown.copy(alpha = 0.3f))
                        )
                        StatItem(
                            icon = Icons.Default.Loyalty, value = "${user.points}", label = "Pts"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                SectionHeader(icon = Icons.Default.Person, title = "Información Personal")

                Spacer(modifier = Modifier.height(14.dp))

                AppTextInput(
                    value = name, onValueChange = { name = it }, label = "Nombre Completo"
                )

                Spacer(modifier = Modifier.height(10.dp))

                AppTextInput(
                    value = email, onValueChange = {}, label = "Email"
                )
                Text(
                    text = "El email no se puede modificar",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                AppTextInput(
                    value = phone, onValueChange = { phone = it }, label = "Teléfono"
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (updateState) {
                    is ProfileViewModel.UpdateState.Loading -> {
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LeatherBrown.copy(alpha = 0.4f),
                                contentColor = TextGray
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Gold, modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GUARDANDO...")
                        }
                    }

                    else -> {
                        Button(
                            onClick = { viewModel.updateProfile(name, email, phone) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = hasChanges,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gold,
                                contentColor = CarbonBlack,
                                disabledContainerColor = LeatherBrown.copy(alpha = 0.4f),
                                disabledContentColor = TextGray
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Guardar Cambios", fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
                SectionDivider()
                Spacer(modifier = Modifier.height(20.dp))

                SectionHeader(icon = Icons.Default.Settings, title = "Preferencias")

                Spacer(modifier = Modifier.height(14.dp))

                PreferenceSwitch(
                    label = "Notificaciones de citas",
                    checked = notifAppointments,
                    onCheckedChange = { notifAppointments = it })
                PreferenceSwitch(
                    label = "Notificaciones de ofertas",
                    checked = notifOffers,
                    onCheckedChange = { notifOffers = it })
                PreferenceSwitch(
                    label = "Recordatorios",
                    checked = notifReminders,
                    onCheckedChange = { notifReminders = it })

                Spacer(modifier = Modifier.height(12.dp))
                SectionDivider()
                Spacer(modifier = Modifier.height(20.dp))

                SectionHeader(icon = Icons.Default.Security, title = "Cuenta")

                Spacer(modifier = Modifier.height(8.dp))

                AccountRow(
                    icon = Icons.Default.Lock,
                    label = "Cambiar contraseña",
                    onClick = { showPasswordDialog = true })
                AccountRow(
                    icon = Icons.Default.Policy,
                    label = "Política de privacidad",
                    onClick = { /* Navigate */ })
                AccountRow(
                    icon = Icons.Default.Description,
                    label = "Términos y condiciones",
                    onClick = { /* Navigate */ })

                Spacer(modifier = Modifier.height(20.dp))
                SectionDivider()
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedLogout),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, RedLogout.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión", fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "HairUp v1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            is ProfileViewModel.ProfileState.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error al cargar perfil", color = TextGray, fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message, color = RedLogout, fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.loadProfile() }, colors = ButtonDefaults.buttonColors(
                            containerColor = Gold, contentColor = CarbonBlack
                        )
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    SnackbarHost(
        hostState = snackbarHostState, modifier = Modifier.padding(16.dp)
    ) { data ->
        Snackbar(
            containerColor = DarkGray, contentColor = White, snackbarData = data
        )
    }


    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = {
                Text(
                    text = "Cerrar sesión", fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Seguro que quieres cerrar sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = RedLogout, contentColor = White
                    ), shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Gold)
                }
            })
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false }, onSave = { current, new ->
            viewModel.changePassword(current, new)
        }, passwordState = passwordState
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    passwordState: ProfileViewModel.PasswordState
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf("") }

    val isLoading = passwordState is ProfileViewModel.PasswordState.Loading
    val errorMessage =
        if (passwordState is ProfileViewModel.PasswordState.Error) passwordState.message else null

    AlertDialog(onDismissRequest = {
        if (!isLoading) {
            onDismiss()
        }
    }, containerColor = DarkGray, titleContentColor = White, title = {
        Text(
            text = "Cambiar contraseña", fontWeight = FontWeight.Bold
        )
    }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AppTextInput(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    localError = ""
                },
                label = "Contraseña actual",
                visualTransformation = PasswordVisualTransformation()
            )
            AppTextInput(
                value = newPassword, onValueChange = {
                    newPassword = it
                    localError = ""
                }, label = "Nueva contraseña", visualTransformation = PasswordVisualTransformation()
            )
            AppTextInput(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    localError = ""
                },
                label = "Confirmar contraseña",
                visualTransformation = PasswordVisualTransformation()
            )

            if (localError.isNotEmpty()) {
                Text(
                    text = localError, color = RedLogout, fontSize = 12.sp
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage, color = RedLogout, fontSize = 12.sp
                )
            }
        }
    }, confirmButton = {
        if (isLoading) {
            Box(
                modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Gold, modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Button(
                onClick = {
                    when {
                        currentPassword.isBlank() -> localError = "Ingresa la contraseña actual"
                        newPassword.isBlank() -> localError = "Ingresa la nueva contraseña"
                        newPassword.length < 6 -> localError = "Mínimo 6 caracteres"
                        newPassword != confirmPassword -> localError =
                            "Las contraseñas no coinciden"

                        else -> onSave(currentPassword, newPassword)
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Gold, contentColor = CarbonBlack
                ), shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar")
            }
        }
    }, dismissButton = {
        if (!isLoading) {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Gold)
            }
        }
    })
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Gold,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextGray)
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Gold,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Gold
        )
    }
}

@Composable
private fun SectionDivider() {
    Divider(color = LeatherBrown.copy(alpha = 0.2f), thickness = 1.dp)
}

@Composable
private fun PreferenceSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = White.copy(alpha = 0.9f)
        )
        Switch(
            checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(
                checkedThumbColor = Gold,
                checkedTrackColor = Gold.copy(alpha = 0.3f),
                uncheckedThumbColor = TextGray,
                uncheckedTrackColor = TextGray.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun AccountRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .clickable { onClick() }
        .padding(horizontal = 4.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = White.copy(alpha = 0.9f),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextGray.copy(alpha = 0.6f),
            modifier = Modifier.size(22.dp)
        )
    }
}