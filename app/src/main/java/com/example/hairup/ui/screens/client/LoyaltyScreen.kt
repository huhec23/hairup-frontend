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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hairup.data.SessionManager
import com.example.hairup.model.Level
import com.example.hairup.ui.components.LevelIcon
import com.example.hairup.ui.components.getLevelColor
import com.example.hairup.ui.viewmodel.RewardViewModel
import com.example.hairup.ui.viewmodel.RewardViewModelFactory

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

private val allLevels = listOf(
    Level(id = 1, name = "Bronce", required = 0, reward = "5% descuento en productos"),
    Level(id = 2, name = "Plata", required = 500, reward = "10% descuento en servicios"),
    Level(
        id = 3,
        name = "Oro",
        required = 1000,
        reward = "Corte gratis cada 10 visitas + 15% descuento"
    ),
    Level(
        id = 4,
        name = "Platino",
        required = 2000,
        reward = "Tratamiento premium gratis + 25% descuento"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: RewardViewModel = viewModel(
        factory = RewardViewModelFactory(sessionManager)
    )

    val rewards by viewModel.rewards.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val redeemResult by viewModel.redeemSuccess.collectAsState()
    val userPoints by viewModel.userPoints.collectAsState()
    val userLevelId by viewModel.userLevelId.collectAsState()

    val currentUser = sessionManager.getUser()

    var selectedRewardId by remember { mutableStateOf<Int?>(null) }
    var showRedeemDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }

    if (currentUser == null) {
        LaunchedEffect(Unit) {
            onBack()
        }
        return
    }

    LaunchedEffect(Unit) {
        viewModel.loadRewards()
    }

    LaunchedEffect(redeemResult) {
        if (redeemResult != null) {
            showResultDialog = true
        }
    }

    val currentLevel = allLevels.firstOrNull { it.id == userLevelId } ?: allLevels.first()
    val nextLevel = allLevels.firstOrNull { it.id == userLevelId + 1 }
    val xpProgress = if (nextLevel != null && nextLevel.required > currentLevel.required) {
        (currentUser.xp - currentLevel.required).toFloat() / (nextLevel.required - currentLevel.required).toFloat()
    } else 1f

    Scaffold(
        containerColor = CarbonBlack, topBar = {
            TopAppBar(
                title = {
                Text(
                    text = "Mi Fidelidad", fontWeight = FontWeight.Bold, color = Gold
                )
            }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Gold
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DarkGray
            )
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(CarbonBlack)
        ) {
            LevelHeader(
                userName = currentUser.name,
                userXp = currentUser.xp,
                userPoints = userPoints,
                currentLevel = currentLevel,
                nextLevel = nextLevel,
                xpProgress = xpProgress
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Niveles y Ventajas")
            Spacer(modifier = Modifier.height(12.dp))
            LevelCardsRow(
                levels = allLevels, currentLevelId = userLevelId
            )

            Spacer(modifier = Modifier.height(28.dp))

            SectionTitle("Recompensas Disponibles")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tienes $userPoints pts para canjear",
                style = MaterialTheme.typography.bodyMedium,
                color = GoldLight,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading && rewards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold)
                }
            } else {
                RewardsSection(
                    rewards = rewards, onRedeemClick = { rewardId ->
                        selectedRewardId = rewardId
                        showRedeemDialog = true
                    })
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFE53935),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showRedeemDialog && selectedRewardId != null) {
        val reward = rewards.find { it.id == selectedRewardId }
        if (reward != null) {
            AlertDialog(
                onDismissRequest = { showRedeemDialog = false },
                containerColor = DarkGray,
                titleContentColor = White,
                textContentColor = TextGray,
                title = {
                    Text(
                        text = "Canjear recompensa", fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = reward.name, fontWeight = FontWeight.Bold, color = Gold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(reward.description)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¿Canjear por ${reward.pointsCost} puntos?",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRedeemDialog = false
                            viewModel.redeemReward(reward.id)
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Gold, contentColor = CarbonBlack
                        ), shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Sí, canjear")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showRedeemDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurface, contentColor = TextGray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar")
                    }
                })
        }
    }

    if (showResultDialog && redeemResult != null) {
        AlertDialog(onDismissRequest = {
            showResultDialog = false
            viewModel.resetRedeemSuccess()
        }, containerColor = DarkGray, titleContentColor = White, icon = {
            if (redeemResult!!.success) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = GreenSuccess,
                    modifier = Modifier.size(40.dp)
                )
            }
        }, title = {
            Text(
                text = if (redeemResult!!.success) "¡Canje exitoso!" else "Error",
                fontWeight = FontWeight.Bold,
                color = if (redeemResult!!.success) GreenSuccess else Color(0xFFE53935)
            )
        }, text = {
            Text(redeemResult!!.message)
        }, confirmButton = {
            Button(
                onClick = {
                    showResultDialog = false
                    viewModel.resetRedeemSuccess()
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Gold, contentColor = CarbonBlack
                ), shape = RoundedCornerShape(8.dp)
            ) {
                Text("Aceptar")
            }
        })
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

@Composable
private fun LevelHeader(
    userName: String,
    userXp: Int,
    userPoints: Int,
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
            LevelIcon(
                levelName = currentLevel.name, size = 64.dp, showGlow = true
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
                text = userName, style = MaterialTheme.typography.bodyLarge, color = TextGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$userXp",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    Text(
                        text = "XP total",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$userPoints",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Text(
                        text = "Puntos",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                val remaining = nextLevel.required - userXp
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
                        levelName = nextLevel.name, size = 18.dp
                    )
                }
            } else {
                Text(
                    text = "Has alcanzado el nivel máximo!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GoldLight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LevelCardsRow(
    levels: List<Level>, currentLevelId: Int
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
    level: Level, isUnlocked: Boolean, isCurrent: Boolean
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
            ), colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) DarkSurface else DarkGray
        ), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrent) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isUnlocked) {
                LevelIcon(
                    levelName = level.name, size = 48.dp, showGlow = isCurrent
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

@Composable
private fun RewardsSection(
    rewards: List<RewardViewModel.RewardItem>, onRedeemClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (rewards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay recompensas disponibles", color = TextGray, fontSize = 14.sp
                )
            }
        } else {
            rewards.forEach { reward ->
                RewardCard(
                    reward = reward, onRedeemClick = { onRedeemClick(reward.id) })
            }
        }
    }
}

@Composable
private fun RewardCard(
    reward: RewardViewModel.RewardItem, onRedeemClick: () -> Unit
) {
    val levelName = when (reward.minLevelId) {
        1 -> "Bronce"
        2 -> "Plata"
        3 -> "Oro"
        4 -> "Platino"
        else -> "Bronce"
    }

    val canRedeem = reward.canAfford && reward.hasRequiredLevel && reward.available

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (canRedeem) DarkGray else DarkGray.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LevelIcon(
                levelName = levelName, size = 36.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (reward.available) White else TextGray
                )

                if (!reward.hasRequiredLevel) {
                    Text(
                        text = "Requiere nivel $levelName",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE53935).copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }

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
                        text = "${reward.pointsCost} pts",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldLight
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onRedeemClick,
                enabled = canRedeem,
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
                    text = if (canRedeem) "Canjear" else "No disponible",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}