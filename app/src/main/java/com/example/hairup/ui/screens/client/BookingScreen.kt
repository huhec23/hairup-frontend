package com.example.hairup.ui.screens.client

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hairup.data.SessionManager
import com.example.hairup.model.Service
import com.example.hairup.ui.viewmodel.BookingViewModel
import com.example.hairup.ui.viewmodel.BookingViewModelFactory
import java.util.Calendar

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val DarkSurface = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val GoldDark = Color(0xFFA68829)
private val LeatherBrown = Color(0xFF8B5E3C)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val GreenSuccess = Color(0xFF4CAF50)


private data class BookingState(
    val selectedService: Service? = null,
    val selectedBarber: BookingViewModel.BarberItem? = null,
    val selectedYear: Int = 0,
    val selectedMonth: Int = 0,
    val selectedDay: Int = 0,
    val selectedTime: String? = null
)

private val stepLabels = listOf("Servicio", "Peluquero", "Fecha", "Confirmar")


private fun getDaysInMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(year, month, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

private fun getFirstDayOfWeekOffset(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(year, month, 1)
    val dow = cal.get(Calendar.DAY_OF_WEEK)
    return (dow + 5) % 7 // Mon=0, Tue=1, ..., Sun=6
}

private fun isSunday(year: Int, month: Int, day: Int): Boolean {
    val cal = Calendar.getInstance()
    cal.set(year, month, day)
    return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
}

private fun isPastDay(year: Int, month: Int, day: Int): Boolean {
    val today = Calendar.getInstance()
    val ty = today.get(Calendar.YEAR)
    val tm = today.get(Calendar.MONTH)
    val td = today.get(Calendar.DAY_OF_MONTH)
    if (year < ty) return true
    if (year == ty && month < tm) return true
    if (year == ty && month == tm && day < td) return true
    return false
}

private fun isToday(year: Int, month: Int, day: Int): Boolean {
    val today = Calendar.getInstance()
    return year == today.get(Calendar.YEAR) && month == today.get(Calendar.MONTH) && day == today.get(
        Calendar.DAY_OF_MONTH
    )
}

private val monthNames = listOf(
    "Enero",
    "Febrero",
    "Marzo",
    "Abril",
    "Mayo",
    "Junio",
    "Julio",
    "Agosto",
    "Septiembre",
    "Octubre",
    "Noviembre",
    "Diciembre"
)

private val dayOfWeekNames = listOf("L", "M", "X", "J", "V", "S", "D")

private fun getDayOfWeekName(year: Int, month: Int, day: Int): String {
    val cal = Calendar.getInstance()
    cal.set(year, month, day)
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Lunes"
        Calendar.TUESDAY -> "Martes"
        Calendar.WEDNESDAY -> "Miércoles"
        Calendar.THURSDAY -> "Jueves"
        Calendar.FRIDAY -> "Viernes"
        Calendar.SATURDAY -> "Sábado"
        else -> "Domingo"
    }
}

private fun formatDateLong(year: Int, month: Int, day: Int): String {
    val dow = getDayOfWeekName(year, month, day)
    val mn = monthNames[month]
    return "$dow $day de $mn, $year"
}

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onBookingComplete: () -> Unit, onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: BookingViewModel = viewModel(
        factory = BookingViewModelFactory(sessionManager)
    )

    val services by viewModel.services.collectAsState()
    val barbers by viewModel.barbers.collectAsState()
    val availableSlots by viewModel.availableSlots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val bookingSuccess by viewModel.bookingSuccess.collectAsState()

    var currentStep by remember { mutableIntStateOf(1) }
    var previousStep by remember { mutableIntStateOf(1) }
    var bookingState by remember { mutableStateOf(BookingState()) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(
        bookingState.selectedBarber,
        bookingState.selectedYear,
        bookingState.selectedMonth,
        bookingState.selectedDay
    ) {
        val barber = bookingState.selectedBarber
        if (barber != null && bookingState.selectedDay > 0) {
            val dateStr = viewModel.formatDateForApi(
                bookingState.selectedYear, bookingState.selectedMonth, bookingState.selectedDay
            )
            viewModel.loadAvailability(barber.id, dateStr)
        }
    }

    LaunchedEffect(bookingSuccess) {
        if (bookingSuccess != null) {
            showSuccessDialog = true
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    val canProceed = when (currentStep) {
        1 -> bookingState.selectedService != null
        2 -> bookingState.selectedBarber != null
        3 -> bookingState.selectedDay > 0 && bookingState.selectedTime != null
        4 -> false
        else -> false
    }

    if (showSuccessDialog) {
        SuccessDialog(
            onDismiss = {
                showSuccessDialog = false
                viewModel.resetBookingSuccess()
                onBookingComplete()
            })
    }

    Scaffold(
        containerColor = CarbonBlack, topBar = {
            TopAppBar(
                title = {
                Text("Reservar Cita", fontWeight = FontWeight.Bold, color = Gold)
            }, navigationIcon = {
                IconButton(onClick = {
                    if (currentStep > 1) {
                        previousStep = currentStep
                        currentStep--
                    } else {
                        onBack()
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Gold)
                }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGray)
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(CarbonBlack)
        ) {
            StepIndicatorRow(currentStep = currentStep)

            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFE53935),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                val goingForward = currentStep >= previousStep
                AnimatedContent(
                    targetState = currentStep, transitionSpec = {
                        if (goingForward) {
                            (slideInHorizontally { it / 3 } + fadeIn()).togetherWith(
                                slideOutHorizontally { -it / 3 } + fadeOut())
                        } else {
                            (slideInHorizontally { -it / 3 } + fadeIn()).togetherWith(
                                slideOutHorizontally { it / 3 } + fadeOut())
                        }
                    }, label = "stepContent"
                ) { step ->
                    when (step) {
                        1 -> Step1ServiceSelection(
                            services = services,
                            selected = bookingState.selectedService,
                            onSelect = { bookingState = bookingState.copy(selectedService = it) },
                            isLoading = isLoading && services.isEmpty()
                        )

                        2 -> Step2BarberSelection(
                            barbers = barbers,
                            selected = bookingState.selectedBarber,
                            onSelect = { bookingState = bookingState.copy(selectedBarber = it) },
                            isLoading = isLoading && barbers.isEmpty()
                        )

                        3 -> Step3DateTimeSelection(
                            state = bookingState,
                            availableSlots = availableSlots,
                            isLoading = isLoading,
                            onDateSelected = { y, m, d ->
                                bookingState = bookingState.copy(
                                    selectedYear = y,
                                    selectedMonth = m,
                                    selectedDay = d,
                                    selectedTime = null
                                )
                            },
                            onTimeSelected = {
                                bookingState = bookingState.copy(selectedTime = it)
                            })

                        4 -> Step4Confirmation(
                            state = bookingState, onConfirmBooking = {
                                val service = bookingState.selectedService
                                val barber = bookingState.selectedBarber
                                val time = bookingState.selectedTime

                                if (service != null && barber != null && time != null) {
                                    val dateStr = viewModel.formatDateForApi(
                                        bookingState.selectedYear,
                                        bookingState.selectedMonth,
                                        bookingState.selectedDay
                                    )
                                    viewModel.createBooking(
                                        serviceId = service.id,
                                        date = dateStr,
                                        time = "$time:00",
                                        barberId = barber.id,
                                        callback = { success ->
                                            if (!success) {

                                            }
                                        })
                                }
                            })
                    }
                }
            }

            if (currentStep < 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentStep > 1) {
                                previousStep = currentStep
                                currentStep--
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold),
                        border = BorderStroke(1.dp, Gold.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (currentStep == 1) "Cancelar" else "Anterior")
                    }

                    Button(
                        onClick = {
                            previousStep = currentStep
                            currentStep++
                        },
                        enabled = canProceed && !isLoading,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = CarbonBlack,
                            disabledContainerColor = LeatherBrown.copy(alpha = 0.3f),
                            disabledContentColor = TextGray.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Siguiente", fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Espaciado en paso 4
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StepIndicatorRow(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        stepLabels.forEachIndexed { index, label ->
            val step = index + 1
            val isCompleted = step < currentStep
            val isCurrent = step == currentStep
            val isActive = step <= currentStep

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> Gold
                                isCurrent -> Gold
                                else -> DarkGray
                            }
                        )
                        .then(
                            if (isCurrent) Modifier.border(2.dp, GoldLight, CircleShape)
                            else Modifier
                        ), contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            null,
                            tint = CarbonBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            "$step",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) CarbonBlack else TextGray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) Gold else TextGray,
                    fontSize = 10.sp
                )
            }

            if (index < stepLabels.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .width(28.dp)
                        .height(2.dp)
                        .background(if (step < currentStep) Gold else DarkGray)
                )
            }
        }
    }
}


@Composable
private fun Step1ServiceSelection(
    services: List<Service>, selected: Service?, onSelect: (Service) -> Unit, isLoading: Boolean
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Gold)
        }
    } else {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Elige tu servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Gold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(services, key = { it.id }) { service ->
                    ServiceCard(
                        service = service,
                        isSelected = selected?.id == service.id,
                        onClick = { onSelect(service) })
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(service: Service, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(2.dp, Gold, RoundedCornerShape(14.dp))
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkSurface else DarkGray
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Gold.copy(alpha = 0.2f) else LeatherBrown.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ContentCut,
                    null,
                    tint = if (isSelected) Gold else TextGray,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    service.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Gold else White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    service.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            null,
                            tint = GoldLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "${service.duration} min",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = GoldLight,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "+${service.xp} XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight
                        )
                    }
                }
            }

            Text(
                "${service.price.toInt()}€",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Gold else White
            )
        }
    }
}

@Composable
private fun Step2BarberSelection(
    barbers: List<BookingViewModel.BarberItem>,
    selected: BookingViewModel.BarberItem?,
    onSelect: (BookingViewModel.BarberItem) -> Unit,
    isLoading: Boolean
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Gold)
        }
    } else {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Elige tu peluquero/a",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Gold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(barbers, key = { it.id }) { barber ->
                    BarberCard(
                        barber = barber,
                        isSelected = selected?.id == barber.id,
                        onClick = { onSelect(barber) })
                }
            }
        }
    }
}

@Composable
private fun BarberCard(
    barber: BookingViewModel.BarberItem, isSelected: Boolean, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(2.dp, Gold, RoundedCornerShape(14.dp))
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkSurface else DarkGray
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Gold.copy(alpha = 0.2f)
                        else LeatherBrown.copy(alpha = 0.15f)
                    )
                    .then(
                        if (isSelected) Modifier.border(2.dp, Gold, CircleShape)
                        else Modifier
                    ), contentAlignment = Alignment.Center
            ) {
                if (barber.initial == "?") {
                    Icon(
                        Icons.Default.Person,
                        null,
                        tint = if (isSelected) Gold else TextGray,
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    Text(
                        barber.initial,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Gold else White
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    barber.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Gold else White
                )
                Text(
                    barber.specialty, style = MaterialTheme.typography.bodySmall, color = TextGray
                )
            }

            if (isSelected) {
                Icon(Icons.Default.Check, null, tint = Gold, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun Step3DateTimeSelection(
    state: BookingState,
    availableSlots: List<String>,
    isLoading: Boolean,
    onDateSelected: (Int, Int, Int) -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val today = Calendar.getInstance()
    var displayYear by remember { mutableIntStateOf(today.get(Calendar.YEAR)) }
    var displayMonth by remember { mutableIntStateOf(today.get(Calendar.MONTH)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Elige fecha y hora",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Gold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        CalendarView(
            displayYear = displayYear,
            displayMonth = displayMonth,
            selectedYear = state.selectedYear,
            selectedMonth = state.selectedMonth,
            selectedDay = state.selectedDay,
            onPrevMonth = {
                if (displayMonth == 0) {
                    displayMonth = 11; displayYear--
                } else displayMonth--
            },
            onNextMonth = {
                if (displayMonth == 11) {
                    displayMonth = 0; displayYear++
                } else displayMonth++
            },
            onDaySelected = { day -> onDateSelected(displayYear, displayMonth, day) })

        if (state.selectedDay > 0) {
            Spacer(modifier = Modifier.height(20.dp))

            val barberName = state.selectedBarber?.name ?: ""
            Text(
                text = if (barberName != "Sin preferencia") "Horario de $barberName"
                else "Horarios disponibles",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Gold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                "${
                    getDayOfWeekName(
                        state.selectedYear, state.selectedMonth, state.selectedDay
                    )
                } " + "${state.selectedDay} de ${monthNames[state.selectedMonth]}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold)
                }
            } else {
                TimeSlotsGrid(
                    slots = availableSlots,
                    selectedTime = state.selectedTime,
                    onTimeSelected = onTimeSelected
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CalendarView(
    displayYear: Int,
    displayMonth: Int,
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: Int,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevMonth) {
                    Icon(Icons.Default.ChevronLeft, "Mes anterior", tint = Gold)
                }
                Text(
                    "${monthNames[displayMonth]} $displayYear",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                IconButton(onClick = onNextMonth) {
                    Icon(Icons.Default.ChevronRight, "Mes siguiente", tint = Gold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                dayOfWeekNames.forEach { name ->
                    Text(
                        name,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (name == "D") LeatherBrown else TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val daysInMonth = getDaysInMonth(displayYear, displayMonth)
            val firstDayOffset = getFirstDayOfWeekOffset(displayYear, displayMonth)
            val totalCells = firstDayOffset + daysInMonth
            val rows = (totalCells + 6) / 7

            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val day = cellIndex - firstDayOffset + 1

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day in 1..daysInMonth) {
                                val past = isPastDay(displayYear, displayMonth, day)
                                val sunday = isSunday(displayYear, displayMonth, day)
                                val disabled = past || sunday
                                val today = isToday(displayYear, displayMonth, day)
                                val selected =
                                    day == selectedDay && displayMonth == selectedMonth && displayYear == selectedYear

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .then(
                                            when {
                                                selected -> Modifier.background(Gold)
                                                today -> Modifier.border(
                                                    1.5.dp, Gold.copy(alpha = 0.5f), CircleShape
                                                )

                                                else -> Modifier
                                            }
                                        )
                                        .then(if (!disabled) Modifier.clickable { onDaySelected(day) }
                                        else Modifier), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "$day",
                                            fontSize = 14.sp,
                                            fontWeight = if (selected || today) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                selected -> CarbonBlack
                                                disabled -> TextGray.copy(alpha = 0.3f)
                                                today -> Gold
                                                else -> White
                                            }
                                        )
                                        if (today && !selected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(Gold)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSlotsGrid(
    slots: List<String>, selectedTime: String?, onTimeSelected: (String) -> Unit
) {
    if (slots.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No hay horarios disponibles para este día", color = TextGray, fontSize = 14.sp
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(280.dp),
            userScrollEnabled = false
        ) {
            items(slots) { time ->
                val isSelected = time == selectedTime

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isSelected) Modifier.background(Gold)
                            else Modifier.background(DarkGray)
                                .border(1.dp, Gold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        )
                        .clickable { onTimeSelected(time) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = time,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CarbonBlack else White
                    )
                }
            }
        }
    }
}


@Composable
private fun Step4Confirmation(
    state: BookingState, onConfirmBooking: () -> Unit
) {
    val service = state.selectedService
    val barber = state.selectedBarber

    if (service == null || barber == null) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Resumen de tu cita",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Gold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                GoldDark, Gold, GoldLight, Gold, GoldDark
                            )
                        )
                    )
            )

            Column(modifier = Modifier.padding(20.dp)) {
                ConfirmationRow(
                    icon = {
                        Icon(
                            Icons.Default.ContentCut,
                            null,
                            tint = Gold,
                            modifier = Modifier.size(20.dp)
                        )
                    }, label = "Servicio", value = service.name
                )
                ConfirmationDetail("${service.duration} min")

                Spacer(modifier = Modifier.height(16.dp))

                ConfirmationRow(
                    icon = {
                        Icon(
                            Icons.Default.Person, null, tint = Gold, modifier = Modifier.size(20.dp)
                        )
                    }, label = "Peluquero/a", value = barber.name
                )

                Spacer(modifier = Modifier.height(16.dp))

                ConfirmationRow(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(20.dp)
                        )
                    }, label = "Fecha", value = formatDateLong(
                        state.selectedYear, state.selectedMonth, state.selectedDay
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                ConfirmationRow(
                    icon = {
                        Icon(
                            Icons.Default.Schedule,
                            null,
                            tint = Gold,
                            modifier = Modifier.size(20.dp)
                        )
                    }, label = "Hora", value = state.selectedTime ?: ""
                )

                Spacer(modifier = Modifier.height(16.dp))

                ConfirmationRow(
                    icon = {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = GoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }, label = "XP que ganarás", value = "+${service.xp} XP"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Gold.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total a pagar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        "${service.price.toInt()}€",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onConfirmBooking,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold, contentColor = CarbonBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Confirmar Reserva",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ConfirmationRow(
    icon: @Composable () -> Unit, label: String, value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextGray)
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ConfirmationDetail(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = TextGray,
        modifier = Modifier.padding(start = 30.dp)
    )
}

@Composable
private fun SuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(GreenSuccess.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = GreenSuccess,
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        title = {
            Text(
                "Cita reservada!",
                fontWeight = FontWeight.Bold,
                color = Gold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                "Tu cita ha sido registrada correctamente. Recibirás una confirmación pronto.",
                color = TextGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold, contentColor = CarbonBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Aceptar", fontWeight = FontWeight.Bold)
            }
        })
}