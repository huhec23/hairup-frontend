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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.hairup.model.AdminAppointment
import com.example.hairup.ui.viewmodel.AdminAppointmentViewModel
import com.example.hairup.ui.viewmodel.AdminAppointmentViewModelFactory

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val GreenConfirmed = Color(0xFF4CAF50)
private val RedCancel = Color(0xFFE53935)
private val BlueCompleted = Color(0xFF64B5F6)

@Composable
fun AdminAppointmentsScreen(stylistId: Int = 0) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: AdminAppointmentViewModel = viewModel(
        factory = AdminAppointmentViewModelFactory(sessionManager)
    )

    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    val todayAppointments = viewModel.todayAppointments
    val upcomingAppointments = viewModel.upcomingAppointments
    val pastAppointments = viewModel.pastAppointments

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Hoy", "Próximas", "Pasadas")

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var targetAppointment by remember { mutableStateOf<AdminAppointment?>(null) }

    LaunchedEffect(stylistId) {
        viewModel.loadAppointments(stylistId)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            viewModel.resetStates()
        }
    }

    LaunchedEffect(operationSuccess) {
        if (operationSuccess) {
            viewModel.resetStates()
        }
    }

    val currentList = when (selectedTab) {
        0 -> todayAppointments
        1 -> upcomingAppointments
        else -> pastAppointments
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkGray,
            contentColor = Gold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = Gold
                )
            }) {
            tabs.forEachIndexed { index, title ->
                val count = when (index) {
                    0 -> todayAppointments.size
                    1 -> upcomingAppointments.size
                    else -> pastAppointments.size
                }
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = {
                    Text(
                        text = "$title ($count)",
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == index) Gold else TextGray
                    )
                })
            }
        }

        if (isLoading && appointments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Gold)
            }
        } else if (errorMessage != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error", color = RedCancel, fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = TextGray,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.loadAppointments(stylistId) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold, contentColor = CarbonBlack
                    )
                ) {
                    Text("Reintentar")
                }
            }
        } else if (currentList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = TextGray.copy(alpha = 0.4f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = when (selectedTab) {
                        0 -> "No hay citas para hoy"
                        1 -> "No hay citas próximas"
                        else -> "No hay citas pasadas"
                    }, style = MaterialTheme.typography.bodyLarge, color = TextGray
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentList.forEach { appointment ->
                    AdminAppointmentCard(
                        appointment = appointment,
                        showStylistName = stylistId == 0,
                        onConfirmClick = {
                            targetAppointment = appointment
                            showConfirmDialog = true
                        },
                        onCancelClick = {
                            targetAppointment = appointment
                            showCancelDialog = true
                        },
                        onCompleteClick = {
                            targetAppointment = appointment
                            showCompleteDialog = true
                        })
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showConfirmDialog && targetAppointment != null) {
        val appt = targetAppointment!!
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false; targetAppointment = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text("Confirmar cita", fontWeight = FontWeight.Bold) },
            text = { Text("¿Confirmar la cita de ${appt.clientName} a las ${appt.timeLabel}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmAppointment(appt.id)
                        showConfirmDialog = false
                        targetAppointment = null
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = GreenConfirmed, contentColor = White
                    ), shape = RoundedCornerShape(8.dp)
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false; targetAppointment = null }) {
                    Text("Volver", color = Gold)
                }
            })
    }

    if (showCancelDialog && targetAppointment != null) {
        val appt = targetAppointment!!
        AlertDialog(
            onDismissRequest = { showCancelDialog = false; targetAppointment = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text("Cancelar cita", fontWeight = FontWeight.Bold) },
            text = { Text("¿Cancelar la cita de ${appt.clientName}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelAppointment(appt.id)
                        showCancelDialog = false
                        targetAppointment = null
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = RedCancel, contentColor = White
                    ), shape = RoundedCornerShape(8.dp)
                ) { Text("Sí, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false; targetAppointment = null }) {
                    Text("No, volver", color = Gold)
                }
            })
    }

    if (showCompleteDialog && targetAppointment != null) {
        val appt = targetAppointment!!
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false; targetAppointment = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text("Completar cita", fontWeight = FontWeight.Bold) },
            text = { Text("¿Marcar como completada la cita de ${appt.clientName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.completeAppointment(appt.id)
                        showCompleteDialog = false
                        targetAppointment = null
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = BlueCompleted, contentColor = White
                    ), shape = RoundedCornerShape(8.dp)
                ) { Text("Completar") }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false; targetAppointment = null }) {
                    Text("Volver", color = Gold)
                }
            })
    }
}

@Composable
private fun AdminAppointmentCard(
    appointment: AdminAppointment,
    showStylistName: Boolean,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(appointment.statusColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (appointment.status) {
                            1 -> Icons.Default.CheckCircle
                            2 -> Icons.Default.Done
                            3 -> Icons.Default.Cancel
                            else -> Icons.Default.Schedule
                        },
                        contentDescription = appointment.statusText,
                        tint = appointment.statusColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = appointment.serviceName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldLight
                    )
                    if (showStylistName) {
                        Text(
                            text = "Con: ${appointment.stylistName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = appointment.timeLabel,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Text(
                        text = appointment.dateLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${appointment.duration} min · ${appointment.price.toInt()}€",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(appointment.statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = appointment.statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = appointment.statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            if (appointment.isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onConfirmClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenConfirmed.copy(alpha = 0.15f),
                            contentColor = GreenConfirmed
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirmar", fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedCancel.copy(alpha = 0.12f), contentColor = RedCancel
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.Cancel, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancelar", fontWeight = FontWeight.Medium)
                    }
                }
            } else if (appointment.isConfirmed) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCompleteClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueCompleted.copy(alpha = 0.15f),
                        contentColor = BlueCompleted
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Default.Done, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Marcar como completada", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}