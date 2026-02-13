package com.example.hairup.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hairup.model.Level
import com.example.hairup.model.User
import com.example.hairup.ui.components.LevelIcon
import com.example.hairup.ui.components.getLevelColor

// Colores del tema
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

// Datos mockeados
private val mockUser = User(
    id = 1,
    email = "maria@example.com",
    name = "Maria Cliente",
    xp = 1250,
    levelId = 3
)

private val allLevels = listOf(
    Level(id = 1, name = "Bronce", required = 0, reward = "5% descuento en productos"),
    Level(id = 2, name = "Plata", required = 500, reward = "10% descuento en servicios"),
    Level(id = 3, name = "Oro", required = 1000, reward = "Corte gratis cada 10 visitas + 15% descuento"),
    Level(id = 4, name = "Platino", required = 2000, reward = "Tratamiento premium gratis + 25% descuento")
)

private val mockAvailablePoints = 650

private data class Reward(
    val name: String,
    val cost: Int,
    val minLevelId: Int
)

private val allRewards = listOf(
    Reward("Muestra de champu gratis", 100, 1),
    Reward("5% descuento proxima cita", 150, 1),
    Reward("Mascarilla capilar gratis", 250, 2),
    Reward("10% descuento en tinte", 300, 2),
    Reward("Corte gratis", 500, 3),
    Reward("Tratamiento hidratante gratis", 400, 3),
    Reward("Sesion completa gratis", 800, 4),
    Reward("Pack productos premium", 700, 4)
)

private data class XpHistoryEntry(
    val description: String,
    val xp: Int
)

private val xpHistory = listOf(
    XpHistoryEntry("Corte y Color", 50),
    XpHistoryEntry("Compra champu reparador", 20),
    XpHistoryEntry("Tinte completo", 60),
    XpHistoryEntry("Compra acondicionador", 15),
    XpHistoryEntry("Mechas", 45)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyScreen(onBack: () -> Unit) {
    val currentLevel = allLevels.first { it.id == mockUser.levelId }
    val nextLevel = allLevels.firstOrNull { it.id == mockUser.levelId + 1 }
    val xpProgress = if (nextLevel != null && nextLevel.required > currentLevel.required) {
        (mockUser.xp - currentLevel.required).toFloat() / (nextLevel.required - currentLevel.required).toFloat()
    } else 1f

    Scaffold(
        containerColor = CarbonBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Fidelidad",
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGray
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(CarbonBlack)
        ) {
            // A) Header - Tu nivel actual
            LevelHeader(
                user = mockUser,
                currentLevel = currentLevel,
                nextLevel = nextLevel,
                xpProgress = xpProgress
            )

            Spacer(modifier = Modifier.height(24.dp))

            // B) Niveles y sus ventajas
            SectionTitle("Niveles y Ventajas")
            Spacer(modifier = Modifier.height(12.dp))
            LevelCardsRow(
                levels = allLevels,
                currentLevelId = mockUser.levelId
            )

            Spacer(modifier = Modifier.height(28.dp))

            // C) Recompensas disponibles
            SectionTitle("Recompensas Disponibles")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tienes $mockAvailablePoints pts para canjear",
                style = MaterialTheme.typography.bodyMedium,
                color = GoldLight,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            RewardsSection(
                currentLevelId = mockUser.levelId,
                availablePoints = mockAvailablePoints
            )

            Spacer(modifier = Modifier.height(28.dp))

            // D) Historial de XP
            SectionTitle("Historial de XP")
            Spacer(modifier = Modifier.height(12.dp))
            XpHistorySection()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Gold,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

// A) Level Header
@Composable
private fun LevelHeader(
    user: User,
    currentLevel: Level,
    nextLevel: Level?,
    xpProgress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        // Gold top accent
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Large level badge - 64dp with glow
            LevelIcon(
                levelName = currentLevel.name,
                size = 64.dp,
                showGlow = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Nivel ${currentLevel.name}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Gold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // XP
            Text(
                text = "${user.xp} XP",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Gold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(LeatherBrown.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(xpProgress.coerceIn(0f, 1f))
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(GoldDark, Gold, GoldLight)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (nextLevel != null) {
                val remaining = nextLevel.required - user.xp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Te faltan $remaining XP para ${nextLevel.name} ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                    LevelIcon(
                        levelName = nextLevel.name,
                        size = 18.dp
                    )
                }
            } else {
                Text(
                    text = "Has alcanzado el nivel maximo!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldLight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// B) Level Cards (horizontal scroll)
@Composable
private fun LevelCardsRow(
    levels: List<Level>,
    currentLevelId: Int
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(levels) { level ->
            val isUnlocked = level.id <= currentLevelId
            val isCurrent = level.id == currentLevelId
            LevelCard(level = level, isUnlocked = isUnlocked, isCurrent = isCurrent)
        }
    }
}

@Composable
private fun LevelCard(
    level: Level,
    isUnlocked: Boolean,
    isCurrent: Boolean
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .then(
                if (isCurrent) Modifier.border(2.dp, Gold, RoundedCornerShape(16.dp))
                else Modifier
            )
            .then(
                if (!isUnlocked) Modifier.alpha(0.5f)
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) DarkSurface else DarkGray
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrent) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge or lock - 48dp
            if (isUnlocked) {
                LevelIcon(
                    levelName = level.name,
                    size = 48.dp,
                    showGlow = isCurrent
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LeatherBrown.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = LeatherBrown.copy(alpha = 0.6f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = level.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) getLevelColor(level.name) else TextGray
            )

            Text(
                text = "${level.required} XP",
                style = MaterialTheme.typography.bodySmall,
                color = if (isUnlocked) GoldLight else TextGray.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = level.reward,
                style = MaterialTheme.typography.bodySmall,
                color = if (isUnlocked) White.copy(alpha = 0.8f) else TextGray.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                maxLines = 3
            )

            if (isCurrent) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "NIVEL ACTUAL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Gold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// C) Rewards Section
@Composable
private fun RewardsSection(
    currentLevelId: Int,
    availablePoints: Int
) {
    val unlockedRewards = allRewards.filter { it.minLevelId <= currentLevelId }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        unlockedRewards.forEach { reward ->
            RewardCard(
                reward = reward,
                canAfford = availablePoints >= reward.cost
            )
        }
    }
}

@Composable
private fun RewardCard(
    reward: Reward,
    canAfford: Boolean
) {
    val levelName = allLevels.firstOrNull { it.id == reward.minLevelId }?.name ?: ""

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkGray),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level badge - 24dp in reward cards
            LevelIcon(
                levelName = levelName,
                size = 36.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${reward.cost} pts",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldLight
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Redeem button
            Button(
                onClick = { /* Canjear */ },
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = CarbonBlack,
                    disabledContainerColor = LeatherBrown.copy(alpha = 0.3f),
                    disabledContentColor = TextGray.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Canjear",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// D) XP History Section
@Composable
private fun XpHistorySection() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        xpHistory.forEach { entry ->
            XpHistoryItem(entry)
        }
    }
}

@Composable
private fun XpHistoryItem(entry: XpHistoryEntry) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkGray),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = entry.description,
                style = MaterialTheme.typography.bodyMedium,
                color = White.copy(alpha = 0.9f)
            )
            Text(
                text = "+${entry.xp} XP",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = GreenSuccess
            )
        }
    }
}
