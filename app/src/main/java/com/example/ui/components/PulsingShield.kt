package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.SafetyStatus
import com.example.ui.theme.KavachCyanGlow
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachEmergencyGlow
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachSafeGreenGlow
import com.example.ui.theme.KavachWarningAmber
import com.example.ui.theme.KavachWarningGlow

@Composable
fun PulsingShield(
    status: SafetyStatus,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val (mainColor, glowColor, icon) = when (status) {
        SafetyStatus.SAFE -> Triple(KavachSafeGreen, KavachSafeGreenGlow, Icons.Default.Shield)
        SafetyStatus.ROUTE_DEVIATION, SafetyStatus.UNEXPECTED_STOP -> Triple(KavachWarningAmber, KavachWarningGlow, Icons.Default.Warning)
        SafetyStatus.EMERGENCY -> Triple(KavachEmergencyRed, KavachEmergencyGlow, Icons.Default.Security)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Outermost animated radar ripple
        Box(
            modifier = Modifier
                .size(size)
                .scale(pulseScale)
                .background(
                    color = mainColor.copy(alpha = pulseAlpha),
                    shape = CircleShape
                )
        )

        // Middle soft glow circle
        Box(
            modifier = Modifier
                .size(size * 0.85f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .border(width = 1.5.dp, color = mainColor.copy(alpha = 0.5f), shape = CircleShape)
        )

        // Inner solid core
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size * 0.65f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(mainColor.copy(alpha = 0.25f), mainColor.copy(alpha = 0.10f))
                    ),
                    shape = CircleShape
                )
                .border(width = 2.dp, color = mainColor, shape = CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.title,
                tint = mainColor,
                modifier = Modifier.size(size * 0.35f)
            )
        }
    }
}
