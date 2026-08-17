package com.example.ui.screens
import com.example.ui.theme.KavachCyanPrimary

import com.example.ui.theme.KavachEmergencyBg
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SafetyStatus
import com.example.ui.theme.KavachAlertCardBg
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachDarkSurfaceVariant
import com.example.ui.theme.KavachEmergencyDarkRed
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachTextDim
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachTextSecondary
import com.example.ui.theme.KavachWarningAmber
import com.example.viewmodel.SafetyViewModel

@Composable
fun SafetyCheckScreen(
    viewModel: SafetyViewModel,
    modifier: Modifier = Modifier
) {
    val countdownSeconds by viewModel.countdownSeconds.collectAsState()
    val safetyStatus by viewModel.safetyStatus.collectAsState()
    val currentAddress by viewModel.currentAddress.collectAsState()
    val currentCoordinates by viewModel.currentCoordinates.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "warning_pulse")
    val alertScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KavachDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "ABHAYA KAVACH",
                    color = KavachTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "PROACTIVE ANOMALY ESCALATION",
                    color = KavachEmergencyRed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(KavachEmergencyRed.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .border(1.dp, KavachEmergencyRed, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "AUTONOMOUS SOS ARMED",
                    color = KavachEmergencyRed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Screen 03 // Safety Check Solid High-Impact Card (Matching Design HTML)
        Card(
            colors = CardDefaults.cardColors(containerColor = KavachAlertCardBg),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .scale(alertScale)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "SCREEN 03 // SAFETY CHECK",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "UNUSUAL\nROUTE",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mega Countdown Display: 28s
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${countdownSeconds}s",
                        color = Color.White,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 64.sp,
                        letterSpacing = (-2).sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Deviation detected from planned route. Please confirm status.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Button 1: I'M SAFE (High contrast White pill with Red text)
                Button(
                    onClick = { viewModel.confirmSafeResponse() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = KavachEmergencyRed
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = KavachEmergencyRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I'M SAFE",
                            color = KavachEmergencyRed,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Button 2: I NEED HELP (Deep red button with border)
                Button(
                    onClick = { viewModel.activateEmergencyMode("Manual 'I NEED HELP' Triggered") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KavachEmergencyDarkRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I NEED HELP",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Telemetry & Escalation Context Box
        Card(
            colors = CardDefaults.cardColors(containerColor = KavachDarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KavachDarkCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ANOMALY GPS TELEMETRY",
                    color = KavachTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentAddress,
                    color = KavachTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Coordinates: $currentCoordinates",
                    color = KavachTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(KavachDarkCardBorder)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AUTONOMOUS ESCALATION",
                        color = KavachWarningAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "SMS + GPS LIVE LINK",
                        color = KavachTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

