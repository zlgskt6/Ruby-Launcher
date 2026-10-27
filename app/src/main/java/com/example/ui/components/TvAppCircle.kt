package com.example.ui.components

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.KnownService
import com.example.model.LauncherCardItem

/**
 * Circular app pills from the "Your apps" row.
 */
@Composable
fun TvAppCircle(
    item: LauncherCardItem,
    isFocused: Boolean,
    onClick: () -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isComposeFocused by interactionSource.collectIsFocusedAsState()
    val active = isFocused || isComposeFocused

    val scale by animateFloatAsState(
        targetValue = if (active) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "circle_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .onFocusChanged { if (it.isFocused) onFocused() }
            .focusable(interactionSource = interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("app_circle_${item.id}")
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(
                    elevation = if (active) 16.dp else 4.dp,
                    shape = CircleShape,
                    spotColor = if (active) Color.White.copy(alpha = 0.6f) else Color.Black
                )
                .clip(CircleShape)
                .then(
                    if (active) {
                        Modifier.border(3.dp, Color.White, CircleShape)
                    } else {
                        Modifier.border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (item.knownService != null) {
                BrandedCircleContent(service = item.knownService)
            } else {
                InstalledAppCircleContent(item = item)
            }
        }
    }
}

@Composable
private fun BrandedCircleContent(service: KnownService) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(service.circleBgColor)),
        contentAlignment = Alignment.Center
    ) {
        when (service) {
            KnownService.NETFLIX -> {
                Text(
                    text = "NETFLIX",
                    color = Color(0xFFE50914),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
            KnownService.PRIME_VIDEO -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "prime",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 13.sp
                    )
                    Text(
                        text = "video",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 11.sp
                    )
                    Canvas(modifier = Modifier.width(36.dp).height(5.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.1f, 2f)
                            quadraticTo(size.width * 0.5f, size.height, size.width * 0.9f, 2f)
                        }
                        drawPath(path, Color.White, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                    }
                }
            }
            KnownService.DISNEY_PLUS -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Disney",
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "+",
                        color = Color(0xFF7CE4FA),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            KnownService.HULU -> {
                Text(
                    text = "hulu",
                    color = Color(0xFF032818),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
            }
            KnownService.TUBI -> {
                Text(
                    text = "tubi",
                    color = Color(0xFFFBBF24),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
            }
            KnownService.YOUTUBE -> {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF0000)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.25f, size.height * 0.15f)
                            lineTo(size.width * 0.85f, size.height * 0.5f)
                            lineTo(size.width * 0.25f, size.height * 0.85f)
                            close()
                        }
                        drawPath(path, Color.White, style = Fill)
                    }
                }
            }
            KnownService.JELLYFIN -> {
                Canvas(modifier = Modifier.size(34.dp)) {
                    val w = size.width
                    val h = size.height
                    val outerPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.12f)
                        lineTo(w * 0.9f, h * 0.82f)
                        lineTo(w * 0.1f, h * 0.82f)
                        close()
                    }
                    val gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFFBD68F8), Color(0xFF00B4D8)),
                        start = Offset(0f, 0f),
                        end = Offset(w, h)
                    )
                    drawPath(outerPath, gradient, style = Stroke(width = 9f, cap = StrokeCap.Round))
                }
            }
            KnownService.PLEX -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(18.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.1f, size.height * 0.1f)
                            lineTo(size.width * 0.65f, size.height * 0.5f)
                            lineTo(size.width * 0.1f, size.height * 0.9f)
                            lineTo(size.width * 0.35f, size.height * 0.9f)
                            lineTo(size.width * 0.9f, size.height * 0.5f)
                            lineTo(size.width * 0.35f, size.height * 0.1f)
                            close()
                        }
                        drawPath(path, Color(0xFFE5A00D), style = Fill)
                    }
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = "PLEX", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            KnownService.MAX -> {
                Text(
                    text = "max",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
            }
            KnownService.SVT_PLAY -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "svt", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.width(2.dp))
                    Canvas(modifier = Modifier.size(12.dp)) {
                        val path = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, size.height * 0.5f)
                            lineTo(0f, size.height)
                            close()
                        }
                        drawPath(path, Color(0xFF00D14E), style = Fill)
                    }
                }
            }
            KnownService.SPOTIFY -> {
                Canvas(modifier = Modifier.size(36.dp)) {
                    drawCircle(Color(0xFF1DB954), radius = size.minDimension / 2)
                    val w = size.width
                    val h = size.height
                    drawArc(
                        color = Color.Black,
                        startAngle = 210f,
                        sweepAngle = 100f,
                        useCenter = false,
                        style = Stroke(width = 4f, cap = StrokeCap.Round),
                        topLeft = Offset(w * 0.22f, h * 0.28f),
                        size = androidx.compose.ui.geometry.Size(w * 0.62f, h * 0.62f)
                    )
                    drawArc(
                        color = Color.Black,
                        startAngle = 210f,
                        sweepAngle = 90f,
                        useCenter = false,
                        style = Stroke(width = 3.5f, cap = StrokeCap.Round),
                        topLeft = Offset(w * 0.28f, h * 0.40f),
                        size = androidx.compose.ui.geometry.Size(w * 0.50f, h * 0.50f)
                    )
                }
            }
            KnownService.APPLE_TV -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "tv", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InstalledAppCircleContent(item: LauncherCardItem) {
    val context = LocalContext.current
    val pm = context.packageManager

    val iconState = produceState<Drawable?>(initialValue = null, key1 = item.packageName) {
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
            .background(Color(0xFF1E222D)),
        contentAlignment = Alignment.Center
    ) {
        val drawable = iconState.value
        if (drawable != null) {
            val bitmap = remember(drawable) {
                try {
                    drawable.toBitmap(80, 80)
                } catch (_: Exception) {
                    null
                }
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = item.title,
                    modifier = Modifier.size(46.dp)
                )
            } else {
                Text(
                    text = item.title.take(3).uppercase(),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Icon(Icons.Default.Apps, contentDescription = item.title, tint = Color.White, modifier = Modifier.size(34.dp))
        }
    }
}
