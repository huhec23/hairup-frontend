package com.example.hairup.ui.screens.client

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hairup.model.BookingStatus

// Theme colors
private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val GoldDark = Color(0xFFA68829)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val AmberYellow = Color(0xFFFFC107)
private val GreenConfirmed = Color(0xFF4CAF50)
private val RedCancel = Color(0xFFE53935)

// Display data class for appointments (combines Booking + Service + stylist info)
private data class AppointmentItem(
    val id: Int,
    val serviceName: String,
    val date: String,
    val dateFormatted: String,
    val time: String,
    val stylistName: String,
    val status: BookingStatus,
    val price: Double,
    val duration: Int,
    val xpEarned: Int = 0
)

// Mock data - Upcoming appointments
private val mockUpcoming = listOf(
    AppointmentItem(
        id = 1,
        serviceName = "Corte y Color",
        date = "2026-02-20",
        dateFormatted = "Viernes 20 Feb, 2026",
        time = "10:00",
        stylistName = "Ana García",
        status = BookingStatus.CONFIRMED,
        price = 45.0,
        duration = 90
    ),
    AppointmentItem(
        id = 2,
        serviceName = "Corte de pelo",
        date = "2026-02-25",
        dateFormatted = "Miércoles 25 Feb, 2026",
        time = "16:30",
        stylistName = "Carlos López",
        status = BookingStatus.PENDING,
        price = 15.0,
        duration = 30
    ),
    AppointmentItem(
        id = 3,
        serviceName = "Tratamiento capilar",
        date = "2026-03-03",
        dateFormatted = "Martes 3 Mar, 2026",
        time = "12:00",
        stylistName = "Laura Martín",
        status = BookingStatus.PENDING,
        price = 25.0,
        duration = 45
    )
)

// Mock data - History
private val mockHistory = listOf(
    AppointmentItem(
        id = 4,
        serviceName = "Corte de pelo",
        date = "2026-02-05",
        dateFormatted = "Jueves 5 Feb, 2026",
        time = "11:00",
        stylistName = "Diego Ruiz",
        status = BookingStatus.COMPLETED,
        price = 15.0,
        duration = 30,
        xpEarned = 25
    ),
    AppointmentItem(
        id = 5,
        serviceName = "Tinte",
        date = "2026-01-28",
        dateFormatted = "Miércoles 28 Ene, 2026",
        time = "14:00",
        stylistName = "Ana García",
        status = BookingStatus.COMPLETED,
        price = 35.0,
        duration = 60,
        xpEarned = 50
    ),
    AppointmentItem(
        id = 6,
        serviceName = "Corte y Barba",
        date = "2026-01-20",
        dateFormatted = "Martes 20 Ene, 2026",
        time = "17:30",
        stylistName = "Carlos López",
        status = BookingStatus.COMPLETED,
        price = 22.0,
        duration = 40,
        xpEarned = 40
    ),
    AppointmentItem(
        id = 7,
        serviceName = "Alisado",
        date = "2026-01-15",
        dateFormatted = "Jueves 15 Ene, 2026",
        time = "10:00",
        stylistName = "Laura Martín",
        status = BookingStatus.CANCELLED,
        price = 40.0,
        duration = 90
    ),
    AppointmentItem(
        id = 8,
        serviceName = "Corte de pelo",
        date = "2026-01-03",
        dateFormatted = "Sábado 3 Ene, 2026",
        time = "9:30",
        stylistName = "Diego Ruiz",
        status = BookingStatus.COMPLETED,
        price = 15.0,
        duration = 30,
        xpEarned = 25
    )
)

@Composable
fun AppointmentsScreen(
    onNavigateToBooking: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var upcomingAppointments by remember { mutableStateOf(mockUpcoming) }
    val tabs = listOf("Próximas", "Historial")

    // Cancel dialog state
    var showCancelDialog by remember { mutableStateOf(false) }
    var appointmentToCancel by remember { mutableStateOf<AppointmentItem?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkGray,
                contentColor = Gold,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 3.dp,
                        color = Gold
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Gold else TextGray
                            )
                        }
                    )
                }
            }

            // Tab content
            when (selectedTab) {
                0 -> UpcomingTab(
                    appointments = upcomingAppointments,
                    onCancelClick = { appointment ->
                        appointmentToCancel = appointment
                        showCancelDialog = true
                    },
                    onNavigateToBooking = onNavigateToBooking
                )
                1 -> HistoryTab(
                    appointments = mockHistory,
                    onNavigateToBooking = onNavigateToBooking
                )
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onNavigateToBooking,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Gold,
            contentColor = CarbonBlack
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Reservar cita"
            )
        }

        // Cancel confirmation dialog
        if (showCancelDialog && appointmentToCancel != null) {
            AlertDialog(
                onDismissRequest = {
                    showCancelDialog = false
                    appointmentToCancel = null
                },
                containerColor = DarkGray,
                titleContentColor = White,
                textContentColor = TextGray,
                title = {
                    Text(
                        text = "Cancelar cita",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("¿Seguro que quieres cancelar esta cita?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            appointmentToCancel?.let { toCancel ->
                                upcomingAppointments = upcomingAppointments.filter { it.id != toCancel.id }
                            }
                            showCancelDialog = false
                            appointmentToCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedCancel,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sí, cancelar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showCancelDialog = false
                            appointmentToCancel = null
                        }
                    ) {
                        Text("No, volver", color = Gold)
                    }
                }
            )
        }
    }
}

@Composable
private fun UpcomingTab(
    appointments: List<AppointmentItem>,
    onCancelClick: (AppointmentItem) -> Unit,
    onNavigateToBooking: () -> Unit
) {
    if (appointments.isEmpty()) {
        EmptyState(
            icon = Icons.Default.EventBusy,
            message = "No tienes citas próximas",
            showBookButton = true,
            onBookClick = onNavigateToBooking
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            appointments.forEach { appointment ->
                UpcomingAppointmentCard(
                    appointment = appointment,
                    onCancelClick = { onCancelClick(appointment) }
                )
            }
            // Bottom spacing for FAB
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun HistoryTab(
    appointments: List<AppointmentItem>,
    onNavigateToBooking: () -> Unit
) {
    if (appointments.isEmpty()) {
        EmptyState(
            icon = Icons.Default.EventBusy,
            message = "Aún no tienes citas en tu historial",
            showBookButton = false,
            onBookClick = onNavigateToBooking
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            appointments.forEach { appointment ->
                HistoryAppointmentCard(appointment = appointment)
            }
            // Bottom spacing for FAB
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun UpcomingAppointmentCard(
    appointment: AppointmentItem,
    onCancelClick: () -> Unit
) {
    val statusColor = when (appointment.status) {
        BookingStatus.PENDING -> AmberYellow
        BookingStatus.CONFIRMED -> GreenConfirmed
        else -> TextGray
    }
    val statusIcon = when (appointment.status) {
        BookingStatus.PENDING -> Icons.Default.Schedule
        BookingStatus.CONFIRMED -> Icons.Default.CheckCircle
        else -> Icons.Default.Schedule
    }
    val statusLabel = when (appointment.status) {
        BookingStatus.PENDING -> "Pendiente"
        BookingStatus.CONFIRMED -> "Confirmada"
        else -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Status icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusLabel,
                    tint = statusColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                // Service name
                Text(
                    text = appointment.serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Date
                Text(
                    text = appointment.dateFormatted,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldLight
                )

                // Time
                Text(
                    text = appointment.time,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Stylist
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.stylistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status chip + duration/price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status chip
                    StatusChip(label = statusLabel, color = statusColor)

                    // Duration + Price
                    Text(
                        text = "${appointment.duration} min · ${appointment.price.toInt()}€",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cancel button
                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedCancel.copy(alpha = 0.12f),
                        contentColor = RedCancel
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cancelar Cita",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryAppointmentCard(appointment: AppointmentItem) {
    val statusColor = when (appointment.status) {
        BookingStatus.COMPLETED -> GreenConfirmed
        BookingStatus.CANCELLED -> RedCancel
        else -> TextGray
    }
    val statusIcon = when (appointment.status) {
        BookingStatus.COMPLETED -> Icons.Default.Done
        BookingStatus.CANCELLED -> Icons.Default.Cancel
        else -> Icons.Default.Schedule
    }
    val statusLabel = when (appointment.status) {
        BookingStatus.COMPLETED -> "Completada"
        BookingStatus.CANCELLED -> "Cancelada"
        else -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Status icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusLabel,
                    tint = statusColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                // Service name
                Text(
                    text = appointment.serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Date
                Text(
                    text = appointment.dateFormatted,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldLight
                )

                // Time
                Text(
                    text = appointment.time,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Stylist
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.stylistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status chip + XP / price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status chip
                    StatusChip(label = statusLabel, color = statusColor)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // XP earned (only for completed)
                        if (appointment.status == BookingStatus.COMPLETED && appointment.xpEarned > 0) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Gold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "+${appointment.xpEarned} XP",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Gold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = "${appointment.price.toInt()}€",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun EmptyState(
    icon: ImageVector,
    message: String,
    showBookButton: Boolean,
    onBookClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextGray.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray
        )

        if (showBookButton) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBookClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = CarbonBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Reservar Cita",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
