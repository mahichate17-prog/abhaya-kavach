package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JourneySession
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachWarningAmber

@Composable
fun InteractiveRouteVisualizer(
    journey: JourneySession?,
    progress: Float,
    isDeviating: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "marker_pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(KavachDarkSurface)
            .border(1.dp, KavachDarkCardBorder, RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height

            // 1. Draw subtle background city road grid
            val gridColor = Color(0xFF1E293B).copy(alpha = 0.6f)
            val stepX = width / 6
            val stepY = height / 5

            for (i in 0..6) {
                drawLine(
                    color = gridColor,
                    start = Offset(i * stepX, 0f),
                    end = Offset(i * stepX, height),
                    strokeWidth = 1f
                )
            }
            for (j in 0..5) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, j * stepY),
                    end = Offset(width, j * stepY),
                    strokeWidth = 1f
                )
            }

            // Waypoint coordinates
            val p0 = Offset(width * 0.12f, height * 0.85f)
            val p1 = Offset(width * 0.28f, height * 0.65f)
            val p2 = Offset(width * 0.50f, height * 0.50f)
            val p3 = Offset(width * 0.74f, height * 0.35f)
            val p4 = Offset(width * 0.88f, height * 0.15f)

            // 2. Draw Safe Verified Corridor (Wide transparent backdrop path)
            val corridorPath = Path().apply {
                moveTo(p0.x, p0.y)
                lineTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                lineTo(p4.x, p4.y)
            }

            drawPath(
                path = corridorPath,
                color = KavachSafeGreen.copy(alpha = 0.12f),
                style = Stroke(
                    width = 24f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 3. Draw Planned Route Line
            drawPath(
                path = corridorPath,
                color = KavachCyanPrimary,
                style = Stroke(
                    width = 6f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 4. Draw Waypoints / Checkpoint Dots
            val waypoints = listOf(p0, p1, p2, p3, p4)
            waypoints.forEachIndexed { index, point ->
                val isStart = index == 0
                val isEnd = index == waypoints.lastIndex
                val dotColor = when {
                    isStart -> KavachSafeGreen
                    isEnd -> KavachCyanPrimary
                    else -> Color(0xFF94A3B8)
                }

                drawCircle(
                    color = KavachDarkBg,
                    radius = 9f,
                    center = point
                )
                drawCircle(
                    color = dotColor,
                    radius = 6f,
                    center = point
                )
            }

            // 5. If Deviating, Draw the Anomaly Path in Amber/Red Dash
            val devPoint1 = Offset(width * 0.52f, height * 0.32f)
            val devPoint2 = Offset(width * 0.44f, height * 0.20f)

            if (isDeviating) {
                val devPath = Path().apply {
                    moveTo(p2.x, p2.y)
                    lineTo(devPoint1.x, devPoint1.y)
                    lineTo(devPoint2.x, devPoint2.y)
                }

                drawPath(
                    path = devPath,
                    color = KavachWarningAmber,
                    style = Stroke(
                        width = 5f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                    )
                )

                // Warning marker at deviation tip
                drawCircle(
                    color = KavachEmergencyRed.copy(alpha = 0.3f),
                    radius = 16f,
                    center = devPoint2
                )
                drawCircle(
                    color = KavachWarningAmber,
                    radius = 7f,
                    center = devPoint2
                )
            }

            // 6. Calculate User Current Position along path
            val currentPos = if (isDeviating) {
                devPoint2
            } else {
                when {
                    progress <= 0.25f -> {
                        val t = progress / 0.25f
                        Offset(p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t)
                    }
                    progress <= 0.50f -> {
                        val t = (progress - 0.25f) / 0.25f
                        Offset(p1.x + (p2.x - p1.x) * t, p1.y + (p2.y - p1.y) * t)
                    }
                    progress <= 0.75f -> {
                        val t = (progress - 0.50f) / 0.25f
                        Offset(p2.x + (p3.x - p2.x) * t, p2.y + (p3.y - p2.y) * t)
                    }
                    else -> {
                        val t = ((progress - 0.75f) / 0.25f).coerceIn(0f, 1f)
                        Offset(p3.x + (p4.x - p3.x) * t, p3.y + (p4.y - p3.y) * t)
                    }
                }
            }

            // 7. Draw Active Live User Tracker Pin with Radar Pulse
            val markerColor = if (isDeviating) KavachWarningAmber else KavachSafeGreen

            drawCircle(
                color = markerColor.copy(alpha = (1f - (pulseRadius / 24f)).coerceIn(0f, 0.6f)),
                radius = pulseRadius,
                center = currentPos
            )
            drawCircle(
                color = KavachDarkBg,
                radius = 10f,
                center = currentPos
            )
            drawCircle(
                color = markerColor,
                radius = 7f,
                center = currentPos
            )
        }

        // Overlay badges: Start / End & GPS Telemetry tag
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(KavachDarkBg.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                .border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (isDeviating) "⚠ ROUTE DEVIATION +450M" else "◉ REAL-TIME GPS MONITOR",
                color = if (isDeviating) KavachWarningAmber else KavachCyanPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .background(KavachDarkBg.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                .border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Safe Corridor: ±50m Threshold",
                color = KavachTextMuted,
                fontSize = 10.sp
            )
        }
    }
}
