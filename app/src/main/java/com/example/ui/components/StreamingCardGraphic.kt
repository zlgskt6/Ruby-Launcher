package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KnownService

/**
 * Dedicated renderer for branded streaming cards that match the user's concept image.
 */
@Composable
fun StreamingCardGraphic(
    service: KnownService,
    modifier: Modifier = Modifier
) {
    when (service) {
        KnownService.JELLYFIN -> JellyfinCard(modifier)
        KnownService.YOUTUBE -> YouTubeCard(modifier)
        KnownService.NETFLIX -> NetflixCard(modifier)
        KnownService.SVT_PLAY -> SvtPlayCard(modifier)
        KnownService.PLEX -> PlexCard(modifier)
        KnownService.PRIME_VIDEO -> PrimeVideoCard(modifier)
        KnownService.DISNEY_PLUS -> DisneyPlusCard(modifier)
        KnownService.SPOTIFY -> SpotifyCard(modifier)
        KnownService.HULU -> HuluCard(modifier)
        KnownService.TUBI -> TubiCard(modifier)
        KnownService.MAX -> MaxCard(modifier)
        KnownService.APPLE_TV -> AppleTvCard(modifier)
    }
}

/**
 * Jellyfin card matching concept screenshot:
 * Deep navy background + glowing purple/cyan triangle logo + clean "Jellyfin" text
 */
@Composable
private fun JellyfinCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF091124)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Jellyfin concentric rounded triangle logo
            Canvas(modifier = Modifier.size(46.dp)) {
                val w = size.width
                val h = size.height

                // Outer gradient triangle path
                val outerPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.12f)
                    lineTo(w * 0.88f, h * 0.80f)
                    lineTo(w * 0.12f, h * 0.80f)
                    close()
                }

                // Inner cutout triangle path
                val innerPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.38f)
                    lineTo(w * 0.72f, h * 0.74f)
                    lineTo(w * 0.28f, h * 0.74f)
                    close()
                }

                val gradient = Brush.linearGradient(
                    colors = listOf(Color(0xFFBD68F8), Color(0xFF5B7FFF), Color(0xFF00B4D8)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )

                drawPath(
                    path = outerPath,
                    brush = gradient,
                    style = Stroke(
                        width = 12f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Center soft dot
                drawCircle(
                    brush = gradient,
                    radius = 4f,
                    center = Offset(w * 0.5f, h * 0.58f)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "Jellyfin",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * YouTube card matching concept screenshot:
 * Clean white background with black "You" and Red badge "Tube"
 */
@Composable
private fun YouTubeCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "You",
                color = Color(0xFF1F1F1F),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.width(3.dp))

            // Iconic red rounded rectangle with "Tube" inside
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFF0000))
                    .padding(horizontal = 9.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tube",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-1).sp
                )
            }
        }
    }
}

/**
 * Netflix card matching concept screenshot:
 * Dark matte background with the iconic bold red "NETFLIX" wordmark
 */
@Composable
private fun NetflixCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF181818)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NETFLIX",
            color = Color(0xFFE50914),
            fontSize = 33.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 3.sp
        )
    }
}

/**
 * SVT Play card matching concept screenshot:
 * Dark charcoal background with bold lowercase "svt" and bright green play triangle
 */
@Composable
private fun SvtPlayCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1015)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "svt",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-1.5).sp
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Bright green play triangle
            Canvas(modifier = Modifier.size(28.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.15f, size.height * 0.1f)
                    lineTo(size.width * 0.95f, size.height * 0.5f)
                    lineTo(size.width * 0.15f, size.height * 0.9f)
                    close()
                }
                drawPath(
                    path = path,
                    color = Color(0xFF00D14E),
                    style = Fill
                )
            }
        }
    }
}

/**
 * Plex card:
 * Dark slate background with amber chevron and gold wordmark
 */
@Composable
private fun PlexCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E2328)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Canvas(modifier = Modifier.size(28.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.1f, size.height * 0.1f)
                    lineTo(size.width * 0.65f, size.height * 0.5f)
                    lineTo(size.width * 0.1f, size.height * 0.9f)
                    lineTo(size.width * 0.35f, size.height * 0.9f)
                    lineTo(size.width * 0.9f, size.height * 0.5f)
                    lineTo(size.width * 0.35f, size.height * 0.1f)
                    close()
                }
                drawPath(path = path, color = Color(0xFFE5A00D), style = Fill)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "PLEX",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

/**
 * Prime Video card:
 * Navy background with "prime video" and bright cyan smile underline
 */
@Composable
private fun PrimeVideoCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF001A30)),
        contentAlignment = Alignment.Center
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "prime",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "video",
                    color = Color(0xFF00A8E1),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Canvas(
                modifier = Modifier
                    .width(120.dp)
                    .height(10.dp)
            ) {
                val path = Path().apply {
                    moveTo(size.width * 0.1f, size.height * 0.3f)
                    quadraticTo(size.width * 0.5f, size.height * 0.95f, size.width * 0.9f, size.height * 0.3f)
                }
                drawPath(
                    path = path,
                    color = Color(0xFF00A8E1),
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * Disney+ card:
 * Deep magical midnight background with "Disney+"
 */
@Composable
private fun DisneyPlusCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF08123B), Color(0xFF020718))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Disney",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "+",
                color = Color(0xFF4EE2FF),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Spotify card:
 * Matte black background with neon green logo
 */
@Composable
private fun SpotifyCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Canvas(modifier = Modifier.size(34.dp)) {
                drawCircle(color = Color(0xFF1DB954), radius = size.minDimension / 2)
                // Sound wave lines
                val w = size.width
                val h = size.height
                val blackColor = Color(0xFF121212)

                drawArc(
                    color = blackColor,
                    startAngle = 210f,
                    sweepAngle = 100f,
                    useCenter = false,
                    style = Stroke(width = 4.5f, cap = StrokeCap.Round),
                    topLeft = Offset(w * 0.22f, h * 0.26f),
                    size = androidx.compose.ui.geometry.Size(w * 0.65f, h * 0.65f)
                )
                drawArc(
                    color = blackColor,
                    startAngle = 210f,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = Stroke(width = 4f, cap = StrokeCap.Round),
                    topLeft = Offset(w * 0.28f, h * 0.38f),
                    size = androidx.compose.ui.geometry.Size(w * 0.52f, h * 0.52f)
                )
                drawArc(
                    color = blackColor,
                    startAngle = 210f,
                    sweepAngle = 80f,
                    useCenter = false,
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round),
                    topLeft = Offset(w * 0.34f, h * 0.50f),
                    size = androidx.compose.ui.geometry.Size(w * 0.40f, h * 0.40f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Spotify",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }
    }
}

/**
 * Hulu card:
 */
@Composable
private fun HuluCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D2818)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "hulu",
            color = Color(0xFF1CE783),
            fontSize = 38.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1.5).sp
        )
    }
}

/**
 * Tubi card:
 */
@Composable
private fun TubiCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF28114B)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "tubi",
            color = Color(0xFFFBBF24),
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp
        )
    }
}

/**
 * Max card:
 */
@Composable
private fun MaxCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF051745)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "max",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1.5).sp
        )
    }
}

/**
 * Apple TV card:
 */
@Composable
private fun AppleTvCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF161A22)),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "tv",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
