package com.example.hairup.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // IMPORTANTE: El asterisco trae size, width, height, etc.
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hairup.ui.components.AppButton

@Composable
fun BookingScreen(
    onBookingComplete: () -> Unit,
    onBack: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    var selectedService by remember { mutableStateOf<String?>(null) }
    var selectedStylist by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Progress Steps
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepIndicator(step = 1, currentStep = currentStep, label = "Servicio")
            StepConnector(active = currentStep >= 2)
            StepIndicator(step = 2, currentStep = currentStep, label = "Estilista")
            StepConnector(active = currentStep >= 3)
            StepIndicator(step = 3, currentStep = currentStep, label = "Fecha")
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            when (currentStep) {
                1 -> ServiceSelection(
                    selected = selectedService,
                    onSelect = { selectedService = it }
                )
                2 -> StylistSelection(
                    selected = selectedStylist,
                    onSelect = { selectedStylist = it }
                )
                3 -> DateSelection()
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = { currentStep-- },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Atrás")
                }
            } else {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
            }

            AppButton(
                text = if (currentStep == 3) "Confirmar" else "Siguiente",
                onClick = {
                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        onBookingComplete()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = (currentStep == 1 && selectedService != null) ||
                        (currentStep == 2 && selectedStylist != null) ||
                        (currentStep == 3)
            )
        }
    }
}

@Composable
fun StepIndicator(step: Int, currentStep: Int, label: String) {
    val isActive = step <= currentStep
    val isCompleted = step < currentStep

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = step.toString(),
                    color = if (isActive) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StepConnector(active: Boolean) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(2.dp)
            .background(
                if (active) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
    )
}

@Composable
fun ServiceSelection(selected: String?, onSelect: (String) -> Unit) {
    val services = listOf("Corte Mujer", "Corte Hombre", "Tinte Completo", "Mechas", "Peinado")
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(services.size) { index ->
            val service = services[index]
            ServiceCard(
                name = service,
                price = "$25.00",
                duration = "45 min",
                isSelected = service == selected,
                onClick = { onSelect(service) }
            )
        }
    }
}

@Composable
fun ServiceCard(name: String, price: String, duration: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ContentCut, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Text(duration, style = MaterialTheme.typography.bodySmall)
            }
            Text(price, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = isSelected, onClick = null)
        }
    }
}

@Composable
fun StylistSelection(selected: String?, onSelect: (String) -> Unit) {
    val stylists = listOf("Ana García", "Carlos Ruiz", "Elena López", "Cualquiera")
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(stylists.size) { index ->
            val stylist = stylists[index]
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(stylist) },
                colors = CardDefaults.cardColors(
                    containerColor = if (stylist == selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(40.dp).background(Color.Gray, CircleShape))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(stylist, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    RadioButton(selected = stylist == selected, onClick = null)
                }
            }
        }
    }
}

@Composable
fun DateSelection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Seleccione Fecha y Hora", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Text("Calendario Placeholder", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {}) { Text("10:00") }
            OutlinedButton(onClick = {}) { Text("11:30") }
            OutlinedButton(onClick = {}) { Text("15:00") }
        }
    }
}