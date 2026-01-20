package com.example.hairup.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.hairup.ui.screens.client.AppointmentsScreen
import com.example.hairup.ui.screens.client.ClientDashboardContent
import com.example.hairup.ui.screens.client.ProfileScreen
import com.example.hairup.ui.screens.client.ShopScreen

@Composable
fun ClientHomeScreen(onNavigateToBooking: () -> Unit) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Inicio", "Citas", "Tienda", "Perfil")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.DateRange,
        Icons.Default.ShoppingBag,
        Icons.Default.Person
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedItem) {
                0 -> ClientDashboardContent(onNewAppointment = onNavigateToBooking)
                1 -> AppointmentsScreen()
                2 -> ShopScreen()
                3 -> ProfileScreen()
            }
        }
    }
}