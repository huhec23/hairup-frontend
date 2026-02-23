package com.example.hairup.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BarBackground = Color(0xFF1A1A1A)
private val GoldActive = Color(0xFFDAA520)
private val GrayInactive = Color(0xFF8A8A8A)
private val TopBorderGold = Color(0xFFDAA520)

data class BottomBarItem(
    val label: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector
)

val clientBottomBarItems = listOf(
    BottomBarItem("Inicio", Icons.Rounded.Home, Icons.Outlined.Home),
    BottomBarItem("Citas", Icons.Rounded.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomBarItem("Tienda", Icons.Rounded.ShoppingBag, Icons.Outlined.ShoppingBag),
    BottomBarItem("Fidelidad", Icons.Rounded.Star, Icons.Outlined.Star),
    BottomBarItem("Perfil", Icons.Rounded.Person, Icons.Outlined.Person)
)

val adminBottomBarItems = listOf(
    BottomBarItem("Dashboard", Icons.Rounded.Dashboard, Icons.Outlined.Dashboard),
    BottomBarItem("Citas", Icons.Rounded.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomBarItem("Productos", Icons.Rounded.Inventory2, Icons.Outlined.Inventory2),
    BottomBarItem("Usuarios", Icons.Rounded.People, Icons.Outlined.People)
)

val adminPrincipalBottomBarItems = listOf(
    BottomBarItem("Dashboard", Icons.Rounded.Dashboard, Icons.Outlined.Dashboard),
    BottomBarItem("Citas", Icons.Rounded.CalendarMonth, Icons.Outlined.CalendarMonth),
    BottomBarItem("Productos", Icons.Rounded.Inventory2, Icons.Outlined.Inventory2),
    BottomBarItem("Servicios", Icons.Rounded.ContentCut, Icons.Outlined.ContentCut),
    BottomBarItem("Usuarios", Icons.Rounded.People, Icons.Outlined.People)
)

@Composable
fun HairUpBottomBar(
    items: List<BottomBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(BarBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(TopBorderGold.copy(alpha = 0.4f))
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                BottomBarTab(
                    item = item,
                    isSelected = selectedIndex == index,
                    onClick = { onItemSelected(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomBarTab(
    item: BottomBarItem, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) GoldActive else GrayInactive,
        animationSpec = tween(durationMillis = 250),
        label = "iconColor"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) GoldActive else GrayInactive,
        animationSpec = tween(durationMillis = 250),
        label = "labelColor"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, indication = ripple(
                    color = GoldActive.copy(alpha = 0.3f)
                ), onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(
                    if (isSelected) GoldActive else Color.Transparent
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Icon(
            imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = labelColor,
            maxLines = 1
        )
    }
}