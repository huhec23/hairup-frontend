package com.example.hairup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hairup.R
import com.example.hairup.ui.components.HairUpBottomBar
import com.example.hairup.ui.components.clientBottomBarItems
import com.example.hairup.ui.screens.client.AppointmentsScreen
import com.example.hairup.ui.screens.client.ClientDashboardContent
import com.example.hairup.ui.screens.client.ProfileScreen
import com.example.hairup.ui.screens.client.ShopScreen

private val CarbonBlack = Color(0xFF121212)
private val Gold = Color(0xFFD4AF37)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    onNavigateToBooking: () -> Unit,
    onNavigateToLoyalty: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(0) }
    var showNotifications by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = CarbonBlack,
        bottomBar = {
            HairUpBottomBar(
                items = clientBottomBarItems,
                selectedIndex = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(CarbonBlack)
        ) {
            // Header con Logo y Notificaciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(48.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(
                            width = 2.dp,
                            color = Gold,
                            shape = CircleShape
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "HairUp Logo",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape),
                        colorFilter = ColorFilter.tint(Gold)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showNotifications = !showNotifications }
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Gold,
                                modifier = Modifier.size(28.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .offset(x = 16.dp, y = (-2).dp)
                                    .clip(CircleShape)
                                    .background(Gold)
                                    .border(1.5.dp, CarbonBlack, CircleShape)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showNotifications,
                        onDismissRequest = { showNotifications = false },
                        offset = DpOffset(0.dp, 8.dp),
                        modifier = Modifier
                            .width(320.dp)
                            .background(Color(0xFF1E1E1E))
                    ) {
                        NotificationDropdownContent()
                    }
                }
            }

            // Contenido según la tab seleccionada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CarbonBlack)
            ) {
                when (selectedItem) {
                    0 -> ClientDashboardContent(
                        onNewAppointment = onNavigateToBooking,
                        onNavigateToShop = { selectedItem = 2 },
                        onNavigateToAppointments = { selectedItem = 1 },
                        onNavigateToLoyalty = onNavigateToLoyalty
                    )
                    1 -> AppointmentsScreen(
                        onNavigateToBooking = onNavigateToBooking
                    )
                    2 -> ShopScreen()
                    3 -> ProfileScreen(onLogout = onLogout)
                }
            }
        }
    }
}

@Composable
private fun NotificationDropdownContent() {
    Column(
        modifier = Modifier
            .background(Color(0xFF1E1E1E))
            .padding(8.dp)
    ) {
        Text(
            text = "Notificaciones",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Gold,
            modifier = Modifier.padding(12.dp)
        )

        Divider(
            color = Color(0xFF8B5E3C).copy(alpha = 0.3f),
            thickness = 1.dp
        )

        NotificationItem(
            title = "¡Cita confirmada!",
            message = "Tu cita para mañana a las 16:30 ha sido confirmada",
            time = "Hace 2h",
            isUnread = true
        )

        Divider(
            color = Color(0xFF8B5E3C).copy(alpha = 0.2f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NotificationItem(
            title = "Nuevo descuento disponible",
            message = "¡20% de descuento en productos de styling!",
            time = "Hace 5h",
            isUnread = true
        )

        Divider(
            color = Color(0xFF8B5E3C).copy(alpha = 0.2f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NotificationItem(
            title = "Puntos ganados",
            message = "Has ganado 50 puntos por tu última cita",
            time = "Ayer",
            isUnread = true
        )

        Divider(
            color = Color(0xFF8B5E3C).copy(alpha = 0.2f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ver todas las notificaciones",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Gold
            )
        }
    }
}

@Composable
private fun NotificationItem(
    title: String,
    message: String,
    time: String,
    isUnread: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(12.dp)
    ) {
        if (isUnread) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Gold)
                    .align(Alignment.Top)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnread) Color(0xFFFFFFFF) else Color(0xFFB0B0B0)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                fontSize = 13.sp,
                color = Color(0xFFB0B0B0),
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = time,
                fontSize = 11.sp,
                color = Color(0xFF8B5E3C)
            )
        }
    }
}
