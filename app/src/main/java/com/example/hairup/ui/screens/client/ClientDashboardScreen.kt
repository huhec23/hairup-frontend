package com.example.hairup.ui.screens.client

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hairup.R
import com.example.hairup.model.Level
import com.example.hairup.model.User
import com.example.hairup.ui.components.LevelIcon
import com.example.hairup.ui.viewmodel.HomeViewModel

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val Gold = Color(0xFFD4AF37)
private val GoldLight = Color(0xFFE2C478)
private val GoldDark = Color(0xFFA68829)
private val LeatherBrown = Color(0xFF8B5E3C)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)

@Composable
fun ClientDashboardContent(
    user: User,
    currentLevel: Level,
    nextLevel: Level?,
    nextAppointment: HomeViewModel.NextAppointment?,
    onNewAppointment: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToLoyalty: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .background(CarbonBlack)
    ) {
        LoyaltyCard(
            user = user,
            currentLevel = currentLevel,
            nextLevel = nextLevel,
            onClick = onNavigateToLoyalty
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Acciones Rapidas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Gold
        )

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.reserva,
                title = "Reservar\nCita",
                onClick = onNewAppointment
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.tienda,
                title = "Tienda",
                onClick = onNavigateToShop
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.calendario,
                title = "Mis Citas",
                onClick = onNavigateToAppointments
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (nextAppointment != null) {
            Text(
                text = "Proxima Cita",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Gold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = nextAppointment.serviceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val dateParts = nextAppointment.date.split("-")
                    val formattedDate = if (dateParts.size == 3) {
                        "${dateParts[2]}/${dateParts[1]}/${dateParts[0]}"
                    } else {
                        nextAppointment.date
                    }

                    Text(
                        text = "$formattedDate, ${nextAppointment.time}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "con ${nextAppointment.stylistName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNewAppointment() },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGray)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.reserva),
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "No tienes citas próximas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            text = "Toca para reservar ahora",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LoyaltyCard(
    user: User, currentLevel: Level, nextLevel: Level?, onClick: () -> Unit
) {
    val xpProgress = if (nextLevel != null && nextLevel.required > currentLevel.required) {
        (user.xp - currentLevel.required).toFloat() / (nextLevel.required - currentLevel.required).toFloat()
    } else 1f
    val xpRemaining = nextLevel?.required?.minus(user.xp) ?: 0

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f, targetValue = 600f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmerOffset"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Gold.copy(alpha = 0.4f), GoldLight.copy(alpha = 0.8f), Gold.copy(alpha = 0.4f)
        ), start = Offset(shimmerOffset, 0f), end = Offset(shimmerOffset + 300f, 100f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.5.dp, brush = shimmerBrush, shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(GoldDark, Gold, GoldLight, Gold, GoldDark)
                    )
                )
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                LevelIcon(
                    levelName = currentLevel.name, size = 52.dp, showGlow = true
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nivel ${currentLevel.name}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Ver fidelidad",
                    tint = Gold.copy(alpha = 0.7f),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${user.xp}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "XP",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldLight,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(LeatherBrown.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(xpProgress.coerceIn(0f, 1f))
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(GoldDark, Gold, GoldLight)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (nextLevel != null) {
                    Text(
                        text = "Faltan $xpRemaining XP para ${nextLevel.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                } else {
                    Text(
                        text = "¡Nivel máximo alcanzado!",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldLight
                    )
                }
                Text(
                    text = "${(xpProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver recompensas",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Gold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    iconRes: Int, title: String, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Card(modifier = modifier
        .clickable { onClick() }
        .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGray),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Gold.copy(alpha = 0.2f)), contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    colorFilter = ColorFilter.tint(Gold),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 15.sp,
                maxLines = 2
            )
        }
    }
}