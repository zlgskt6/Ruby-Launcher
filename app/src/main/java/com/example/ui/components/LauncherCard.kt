package com.example.ui.components

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.CardSize
import com.example.model.LauncherCardItem
import com.example.model.LauncherThemeOption

@Composable
fun LauncherCard(
    item: LauncherCardItem,
    isFocused: Boolean,
    cardSize: CardSize,
    theme: LauncherThemeOption,
    onClick: () -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isComposeFocused by interactionSource.collectIsFocusedAsState()
    val active = isFocused || isComposeFocused

    val scale by animateFloatAsState(
        targetValue = if (active) 1.05f else 0.98f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f),
        label = "card_scale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (active) 0.55f else 0f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f),
        label = "glow_alpha"
    )

    val shape = RoundedCornerShape(10.dp)
    val themeColor = Color(theme.primaryHex)

    val glowBrush = Brush.radialGradient(
        colors = listOf(
            themeColor.copy(alpha = glowAlpha),
            themeColor.copy(alpha = glowAlpha * 0.4f),
            Color.Transparent
        )
    )

    Box(
        modifier = modifier
            .width(cardSize.widthDp.dp)
            .height(cardSize.heightDp.dp)
            .scale(scale)
            // Diffuse glowing ambient aura under the focused card (as in concept image)
            .drawBehind {
                if (glowAlpha > 0.01f) {
                    drawRect(
                        brush = glowBrush,
                        topLeft = androidx.compose.ui.geometry.Offset(-size.width * 0.15f, size.height * 0.7f),
                        size = androidx.compose.ui.geometry.Size(size.width * 1.3f, size.height * 0.65f)
                    )
                }
            }
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged { state ->
                if (state.isFocused) {
                    onFocused()
                }
            }
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("launcher_card_${item.id}"),
        contentAlignment = Alignment.Center
    ) {
        // Inner card container with crisp border when focused
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = if (active) 14.dp else 4.dp,
                    shape = shape,
                    clip = false
                )
                .clip(shape)
                .background(Color(0xFF161616))
                .then(
                    if (active) {
                        Modifier.border(
                            width = 2.5.dp,
                            color = themeColor.copy(alpha = 0.9f),
                            shape = shape
                        )
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.06f),
                            shape = shape
                        )
                    }
                )
        ) {
            if (item.knownService != null) {
                // Branded streaming card (Jellyfin, YouTube, Netflix, SVT Play, etc.)
                StreamingCardGraphic(
                    service = item.knownService,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Generic installed Android app
                InstalledAppCardContent(item)
            }

            // Installed badge indicator (subtle dot in top right)
            if (item.isInstalled) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.85f))
                )
            }
        }
    }
}

/**
 * Clean card representation for installed apps without custom streaming graphics
 */
@Composable
private fun InstalledAppCardContent(item: LauncherCardItem) {
    val context = LocalContext.current
    val pm = context.packageManager

    val appIconState = produceState<Drawable?>(initialValue = null, key1 = item.packageName) {
        if (!item.packageName.isNullOrEmpty()) {
            try {
                value = pm.getApplicationIcon(item.packageName)
            } catch (_: Exception) {
                value = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF22262E), Color(0xFF11141A))
                )
            )
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val drawable = appIconState.value
            if (drawable != null) {
                val bitmap = remember(drawable) {
                    try {
                        drawable.toBitmap(128, 128)
                    } catch (_: Exception) {
                        null
                    }
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = item.title,
                        modifier = Modifier.size(54.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = item.title,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
