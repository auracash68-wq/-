package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LocalizedStrings
import com.example.ui.ScreenDestination
import com.example.ui.theme.CardWhite
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.RobotoFontFamily
import com.example.ui.theme.UnselectedNavColor

/**
 * Recreates the exact 3-item Bottom Navigation Bar from Reference Image 2:
 * Home | Analytics | Settings
 * White container with rounded upper corners, bright blue selected item, muted navy/grey unselected items.
 */
@Composable
fun HisabBottomNavigationBar(
    currentScreen: ScreenDestination,
    strings: LocalizedStrings,
    onSelectDestination: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                ambientColor = Color(0x26071D4B),
                spotColor = Color(0x26071D4B)
            ),
        color = CardWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Filled.Home,
                label = strings.home,
                selected = currentScreen == ScreenDestination.HOME,
                testTag = "nav_home_button",
                onClick = { onSelectDestination(ScreenDestination.HOME) }
            )
            BottomNavItem(
                icon = Icons.Filled.BarChart,
                label = strings.analytics,
                selected = currentScreen == ScreenDestination.ANALYTICS,
                testTag = "nav_analytics_button",
                onClick = { onSelectDestination(ScreenDestination.ANALYTICS) }
            )
            BottomNavItem(
                icon = Icons.Filled.Settings,
                label = strings.settings,
                selected = currentScreen == ScreenDestination.SETTINGS,
                testTag = "nav_settings_button",
                onClick = { onSelectDestination(ScreenDestination.SETTINGS) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val tint = if (selected) PrimaryBlue else UnselectedNavColor
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = tint,
            fontFamily = RobotoFontFamily,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.5.sp
        )
    }
}
