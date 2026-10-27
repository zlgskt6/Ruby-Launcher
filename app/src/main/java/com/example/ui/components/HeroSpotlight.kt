package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KnownService
import com.example.model.LauncherCardItem
import com.example.model.WatchProgressItem

/**
 * Hero feature area powered strictly by the user's active content,
 * personal streaming apps, and continue watching queue (no ads).
 */
@Composable
fun HeroSpotlight(
    watchItem: WatchProgressItem?,
    focusedApp: LauncherCardItem?,
    onPlayClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val service = watchItem?.service ?: focusedApp?.knownService
    val title = watchItem?.title ?: focusedApp?.title ?: "Ruby TV"
    val subtitle = watchItem?.subtitle ?: "Your personal streaming library • Ad-free & focused"

    // Background gradient adapting to the active media/service
    val heroBrush = when (service) {
        KnownService.DISNEY_PLUS -> Brush.radialGradient(
            colors = listOf(Color(0xFF261D52), Color(0xFF140F2E), Color(0xFF080714)),
            center = Offset(1100f, 250f),
            radius = 900f
        )
        KnownService.NETFLIX -> Brush.radialGradient(
            colors = listOf(Color(0xFF381215), Color(0xFF1F0B0D), Color(0xFF0A0203)),
            center = Offset(1100f, 250f),
            radius = 900f
        )
        KnownService.JELLYFIN -> Brush.radialGradient(
            colors = listOf(Color(0xFF162554), Color(0xFF0B1430), Color(0xFF050917)),
            center = Offset(1100f, 250f),
            radius = 900f
        )
        KnownService.PRIME_VIDEO -> Brush.radialGradient(
            colors = listOf(Color(0xFF113254), Color(0xFF091C30), Color(0xFF040A12)),
            center = Offset(1100f, 250f),
            radius = 900f
        )
        KnownService.APPLE_TV -> Brush.radialGradient(
            colors = listOf(Color(0xFF2B333D), Color(0xFF161B21), Color(0xFF0A0C0F)),
            center = Offset(1100f, 250f),
            radius = 900f
        )
        KnownService.YOUTUBE -> Brush.radialGradient(
            colors = listOf(Color(0xFF3B1515), Color(0xFF1F0909), Color(0xFF0C0303)),
            center = Offset(1100f, 250f),
            radius = 900f
        )
        else -> Brush.radialGradient(
            colors = listOf(Color(0xFF1A2233), Color(0xFF0F1522), Color(0xFF080B12)),
            center = Offset(1100f, 250f),
            radius = 900f
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(heroBrush)
            .testTag("hero_spotlight")
    ) {
        // Soft vignette to seamlessly blend into bottom rows
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF0A0C14).copy(alpha = 0.95f)),
                        startY = 140f
                    )
                )
        )

        // Hero Content layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Left: Title, Metadata, Service badge, and Quick Play button
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Service Badge
                if (service != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = service.displayName,
                            color = Color(0xFF8AB4F8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.5f))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Your Subscription",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }

                // Title
                AnimatedContent(
                    targetState = title,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith
                                fadeOut(animationSpec = tween(150))
                    },
                    label = "hero_title"
                ) { targetTitle ->
                    Text(
                        text = targetTitle,
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 40.sp,
                        modifier = Modifier.testTag("hero_title_text")
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle metadata
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.testTag("hero_subtitle_text")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Play / Open action button
                Button(
                    onClick = onPlayClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.testTag("hero_play_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (watchItem != null) "Resume" else "Open App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Right: Carousel dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                repeat(5) { index ->
                    val isCurrent = index == 0
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) Color.White else Color.White.copy(alpha = 0.35f))
                    )
                }
            }
        }
    }
}
