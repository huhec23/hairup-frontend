package com.example.hairup.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hairup.ui.components.HairUpBottomBar
import com.example.hairup.ui.components.adminBottomBarItems

private val CarbonBlack = Color(0xFF121212)

@Composable
fun AdminHomeScreen() {
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = CarbonBlack,
        bottomBar = {
            HairUpBottomBar(
                items = adminBottomBarItems,
                selectedIndex = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(CarbonBlack)
        ) {
            when (selectedItem) {
                0 -> AdminDashboardContent()
                1 -> AdminAppointmentsScreen()
                2 -> AdminProductsScreen()
                3 -> AdminClientsScreen()
            }
        }
    }
}

@Composable
fun AdminDashboardContent() {
    AdminDashboardScreen()
}

@Composable
fun AdminClientsScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Gestión de Clientes", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Lista de clientes registrados...")
    }
}
