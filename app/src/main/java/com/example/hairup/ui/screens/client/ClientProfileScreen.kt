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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hairup.ui.components.AppTextInput

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
private val RedLogout = Color(0xFFE53935)

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {}
) {
    // Form state
    var name by remember { mutableStateOf("María Cliente") }
    var email by remember { mutableStateOf("maria@email.com") }
    var phone by remember { mutableStateOf("666 123 456") }

    // Track initial values
    val initialName = remember { "María Cliente" }
    val initialPhone = remember { "666 123 456" }
    val hasChanges = name != initialName || phone != initialPhone

    // Preferences state
    var notifAppointments by remember { mutableStateOf(true) }
    var notifOffers by remember { mutableStateOf(true) }
    var notifReminders by remember { mutableStateOf(true) }

    // Dialog states
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ===== A) Profile Header =====
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GoldDark, Gold, GoldLight)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = CarbonBlack
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name + Level badge
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "María Cliente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Level badge
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
                        text = "Oro",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "maria@email.com",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== B) Quick Stats Card =====
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
                    value = "12",
                    label = "Citas"
                )
                // Vertical divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(LeatherBrown.copy(alpha = 0.3f))
                )
                StatItem(
                    icon = Icons.Default.Star,
                    value = "1250",
                    label = "XP"
                )
                // Vertical divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(LeatherBrown.copy(alpha = 0.3f))
                )
                StatItem(
                    icon = Icons.Default.Loyalty,
                    value = "650",
                    label = "Pts"
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ===== C) Personal Information =====
        SectionHeader(icon = Icons.Default.Person, title = "Información Personal")

        Spacer(modifier = Modifier.height(14.dp))

        AppTextInput(
            value = name,
            onValueChange = { name = it },
            label = "Nombre Completo"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Email - read-only
        AppTextInput(
            value = email,
            onValueChange = {},
            label = "Email"
        )
        Text(
            text = "El email no se puede modificar",
            style = MaterialTheme.typography.bodySmall,
            color = TextGray.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppTextInput(
            value = phone,
            onValueChange = { phone = it },
            label = "Teléfono"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = { /* Save changes */ },
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
                text = "Guardar Cambios",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
        SectionDivider()
        Spacer(modifier = Modifier.height(20.dp))

        // ===== D) Preferences =====
        SectionHeader(icon = Icons.Default.Settings, title = "Preferencias")

        Spacer(modifier = Modifier.height(14.dp))

        PreferenceSwitch(
            label = "Notificaciones de citas",
            checked = notifAppointments,
            onCheckedChange = { notifAppointments = it }
        )
        PreferenceSwitch(
            label = "Notificaciones de ofertas",
            checked = notifOffers,
            onCheckedChange = { notifOffers = it }
        )
        PreferenceSwitch(
            label = "Recordatorios",
            checked = notifReminders,
            onCheckedChange = { notifReminders = it }
        )

        Spacer(modifier = Modifier.height(12.dp))
        SectionDivider()
        Spacer(modifier = Modifier.height(20.dp))

        // ===== E) Account =====
        SectionHeader(icon = Icons.Default.Security, title = "Cuenta")

        Spacer(modifier = Modifier.height(8.dp))

        AccountRow(
            icon = Icons.Default.Lock,
            label = "Cambiar contraseña",
            onClick = { showPasswordDialog = true }
        )
        AccountRow(
            icon = Icons.Default.Policy,
            label = "Política de privacidad",
            onClick = { /* Navigate */ }
        )
        AccountRow(
            icon = Icons.Default.Description,
            label = "Términos y condiciones",
            onClick = { /* Navigate */ }
        )

        Spacer(modifier = Modifier.height(20.dp))
        SectionDivider()
        Spacer(modifier = Modifier.height(20.dp))

        // ===== F) Logout Button =====
        OutlinedButton(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedLogout),
            border = androidx.compose.foundation.BorderStroke(1.dp, RedLogout.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrar Sesión",
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== G) App Version =====
        Text(
            text = "HairUp v1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = TextGray.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    // ===== Dialogs =====

    // Logout confirmation
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = {
                Text(
                    text = "Cerrar sesión",
                    fontWeight = FontWeight.Bold
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedLogout,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Gold)
                }
            }
        )
    }

    // Change password dialog
    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onSave = { showPasswordDialog = false }
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkGray,
        titleContentColor = White,
        title = {
            Text(
                text = "Cambiar contraseña",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppTextInput(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "Contraseña actual",
                    visualTransformation = PasswordVisualTransformation()
                )
                AppTextInput(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Nueva contraseña",
                    visualTransformation = PasswordVisualTransformation()
                )
                AppTextInput(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirmar contraseña",
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && newPassword == confirmPassword,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = CarbonBlack,
                    disabledContainerColor = LeatherBrown.copy(alpha = 0.4f),
                    disabledContentColor = TextGray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Gold)
            }
        }
    )
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextGray
        )
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
    Divider(
        color = LeatherBrown.copy(alpha = 0.2f),
        thickness = 1.dp
    )
}

@Composable
private fun PreferenceSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Gold,
                checkedTrackColor = Gold.copy(alpha = 0.3f),
                uncheckedThumbColor = TextGray,
                uncheckedTrackColor = TextGray.copy(alpha = 0.2f),
                uncheckedBorderColor = TextGray.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun AccountRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = TextGray.copy(alpha = 0.6f),
            modifier = Modifier.size(22.dp)
        )
    }
}
