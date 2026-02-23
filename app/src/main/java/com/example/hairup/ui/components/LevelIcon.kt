package com.example.hairup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val BronzeColor = Color(0xFFCD7F32)
private val SilverColor = Color(0xFFC0C0C0)
private val OroColor = Color(0xFFFFD700)
private val PlatinumColor = Color(0xFFB9F2FF)

fun getLevelColor(levelName: String): Color = when (levelName) {
    "Bronce" -> BronzeColor
    "Plata" -> SilverColor
    "Oro" -> OroColor
    "Platino" -> PlatinumColor
    else -> OroColor
}

@Composable
fun LevelIcon(
    levelName: String, size: Dp, modifier: Modifier = Modifier, showGlow: Boolean = false
) {
    val levelColor = getLevelColor(levelName)
    val icon = if (levelName == "Platino") Icons.Filled.Diamond else Icons.Filled.WorkspacePremium
    val iconSize = size * 0.55f

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(levelColor.copy(alpha = 0.15f))
            .then(
                if (showGlow) Modifier.border(
                    width = 2.5.dp, brush = Brush.linearGradient(
                        colors = listOf(
                            levelColor.copy(alpha = 0.6f), levelColor, levelColor.copy(alpha = 0.6f)
                        )
                    ), shape = CircleShape
                ) else Modifier
            ), contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = levelName,
            tint = levelColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
