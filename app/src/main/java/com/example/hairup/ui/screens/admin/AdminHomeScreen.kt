package com.example.hairup.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hairup.data.SessionManager
import com.example.hairup.ui.components.HairUpBottomBar
import com.example.hairup.ui.components.adminBottomBarItems
import com.example.hairup.ui.components.adminPrincipalBottomBarItems

private val CarbonBlack = Color(0xFF121212)
private val Gold = Color(0xFFD4AF37)
private val TextGray = Color(0xFFB0B0B0)

@Composable
fun AdminHomeScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val currentUser = sessionManager.getUser()

    var selectedItem by remember { mutableIntStateOf(0) }


    val isAdminPrincipal = currentUser?.id == 1 || currentUser?.email == "admin@hairup.com"

    val bottomBarItems = if (isAdminPrincipal) adminPrincipalBottomBarItems else adminBottomBarItems

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            onLogout()
        }
    }

    Scaffold(
        containerColor = CarbonBlack, bottomBar = {
            HairUpBottomBar(
                items = bottomBarItems,
                selectedIndex = selectedItem,
                onItemSelected = { selectedItem = it })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(CarbonBlack)
        ) {
            AdminHeader(
                stylistName = currentUser?.name ?: "Admin", onLogout = onLogout
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CarbonBlack)
            ) {
                if (isAdminPrincipal) {
                    when (selectedItem) {
                        0 -> AdminDashboardScreen()
                        1 -> AdminAppointmentsScreen(stylistId = 0)
                        2 -> AdminProductsScreen()
                        3 -> AdminServicesScreen()
                        4 -> AdminUsersScreen()
                    }
                } else {
                    when (selectedItem) {
                        0 -> AdminDashboardScreen()
                        1 -> AdminAppointmentsScreen(stylistId = currentUser?.id ?: 0)
                        2 -> AdminProductsScreen()
                        3 -> AdminUsersScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminHeader(stylistName: String, onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Gold.copy(alpha = 0.2f)), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stylistName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
                Text(
                    text = "Panel de gestión",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
        }

        IconButton(onClick = onLogout) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Cerrar sesión",
                tint = TextGray,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}