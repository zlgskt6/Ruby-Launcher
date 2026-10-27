package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LauncherThemeOption

@Composable
fun TopHeaderBar(
    timeString: String,
    dateString: String,
    showDate: Boolean,
    isWifiConnected: Boolean,
    theme: LauncherThemeOption,
    onWifiClick: () -> Unit,
    onAllAppsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Time & Date (matching concept image "20:52 Thursday 29 October")
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = timeString,
                color = Color.White,
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.testTag("clock_display")
            )

            if (showDate && dateString.isNotEmpty()) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = dateString,
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(bottom = 7.dp)
                        .testTag("date_display")
                )
            }
        }

        // Right: Status & Quick Controls Pill (as seen in concept image)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF22242B).copy(alpha = 0.85f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("top_status_pill")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Wi-Fi Button
                PillIconButton(
                    icon = if (isWifiConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = "Wi-Fi Settings",
                    tint = if (isWifiConnected) Color.White else Color(0xFFEF4444),
                    theme = theme,
                    testTag = "wifi_status_button",
                    onClick = onWifiClick
                )

                // All Apps drawer quick access
                PillIconButton(
                    icon = Icons.Default.Apps,
                    contentDescription = "All Installed Apps",
                    tint = Color.White,
                    theme = theme,
                    testTag = "all_apps_button",
                    onClick = onAllAppsClick
                )

                // Settings Button
                PillIconButton(
                    icon = Icons.Default.Settings,
                    contentDescription = "Launcher Settings",
                    tint = Color.White,
                    theme = theme,
                    testTag = "settings_button",
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
private fun PillIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    theme: LauncherThemeOption,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val themeColor = Color(theme.primaryHex)

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(if (isFocused) themeColor.copy(alpha = 0.25f) else Color.Transparent)
            .then(
                if (isFocused) {
                    Modifier.border(1.5.dp, themeColor, CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .focusable(interactionSource = interactionSource)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isFocused) themeColor else tint,
            modifier = Modifier.size(22.dp)
        )
    }
}
