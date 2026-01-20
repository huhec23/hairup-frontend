package com.example.hairup.ui.screens.client

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppointmentsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Próximas", "Historial")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            if (selectedTab == 0) {
                Text("Lista de próximas citas...")
                // TODO: Implementar lista
            } else {
                Text("Historial de citas anteriores...")
                // TODO: Implementar lista
            }
        }
    }
}
