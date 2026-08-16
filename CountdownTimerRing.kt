package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachWarningAmber

@Composable
fun CountdownTimerRing(
    secondsRemaining: Int,
    totalSeconds: Int = 30,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp
) {
    val progress = (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    val targetColor = when {
        secondsRemaining > 15 -> KavachWarningAmber
        secondsRemaining > 5 -> Color(0xFFF97316) // Deep orange
        else -> KavachEmergencyRed
    }

    val ringColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(500),
        label = "ring_color"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 10.dp.toPx()

            // Background inactive track
            drawCircle(
                color = Color(0xFF1E293B),
                style = Stroke(width = strokeWidth)
            )

            // Active remaining time arc
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${secondsRemaining}s",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = ringColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "REMAINING",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = KavachTextMuted,
                letterSpacing = 1.sp
            )
        }
    }
}
