package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KavachAlertCardBg
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachEmergencyDarkRed
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachLavender
import com.example.ui.theme.KavachLavenderBg
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachSafeGreenBg
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
    val countdownSeconds by
        viewModel.countdownSeconds.collectAsState()

    val currentAddress by
        viewModel.currentAddress.collectAsState()

    val currentCoordinates by
        viewModel.currentCoordinates.collectAsState()

    val scrollState = rememberScrollState()

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "warning_pulse"
        )

    val alertScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                650,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KavachDarkBg)
            .verticalScroll(scrollState)
            .padding(
                horizontal = 18.dp,
                vertical = 20.dp
            )
    ) {

        // ─────────────────────────────
        // HEADER
        // ─────────────────────────────

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            KavachLavenderBg,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.Security,
                        contentDescription = null,
                        tint = KavachLavender,
                        modifier =
                            Modifier.size(22.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Column {
                    Text(
                        text = "ABHAYA KAVACH",
                        color =
                            KavachTextPrimary,
                        fontSize = 18.sp,
                        fontWeight =
                            FontWeight.Black,
                        letterSpacing =
                            (-0.5).sp
                    )

                    Text(
                        text = "SAFETY CHECK",
                        color =
                            KavachEmergencyRed,
                        fontSize = 9.sp,
                        fontWeight =
                            FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        KavachEmergencyBg,
                        RoundedCornerShape(50.dp)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
            ) {
                Text(
                    text = "SOS ARMED",
                    color =
                        KavachEmergencyRed,
                    fontSize = 9.sp,
                    fontWeight =
                        FontWeight.Black
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(18.dp)
        )

        // ─────────────────────────────
        // MAIN ALERT CARD
        // ─────────────────────────────

        Card(
            colors = CardDefaults.cardColors(
                containerColor =
                    KavachAlertCardBg
            ),
            shape =
                RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .scale(alertScale)
        ) {

            Column(
                modifier =
                    Modifier.padding(22.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center,
                    modifier = Modifier
                        .size(62.dp)
                        .background(
                            Color.White.copy(
                                alpha = 0.16f
                            ),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            Color.White.copy(
                                alpha = 0.25f
                            ),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.Warning,
                        contentDescription =
                            "Warning",
                        tint = Color.White,
                        modifier =
                            Modifier.size(32.dp)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text = "UNUSUAL ROUTE",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight =
                        FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        "We noticed something unexpected.",
                    color =
                        Color.White.copy(
                            alpha = 0.85f
                        ),
                    fontSize = 12.sp,
                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                // COUNTDOWN
                Box(
                    contentAlignment =
                        Alignment.Center,
                    modifier = Modifier
                        .size(128.dp)
                        .background(
                            Color.White.copy(
                                alpha = 0.12f
                            ),
                            CircleShape
                        )
                        .border(
                            2.dp,
                            Color.White.copy(
                                alpha = 0.4f
                            ),
                            CircleShape
                        )
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Text(
                            text =
                                "${countdownSeconds}s",
                            color =
                                Color.White,
                            fontSize = 48.sp,
                            fontWeight =
                                FontWeight.Black,
                            lineHeight = 50.sp
                        )

                        Text(
                            text = "TO RESPOND",
                            color =
                                Color.White.copy(
                                    alpha = 0.75f
                                ),
                            fontSize = 8.sp,
                            fontWeight =
                                FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                Text(
                    text =
                        "Deviation detected from your planned route. Confirm that you're safe or ask for help.",
                    color =
                        Color.White.copy(
                            alpha = 0.9f
                        ),
                    fontSize = 12.sp,
                    fontWeight =
                        FontWeight.Medium,
                    textAlign =
                        TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )

                // I'M SAFE
                Button(
                    onClick = {
                        viewModel.confirmSafeResponse()
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color.White,
                            contentColor =
                                KavachEmergencyRed
                        ),
                    shape =
                        RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier =
                            Modifier.size(20.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text = "I'M SAFE",
                        fontSize = 15.sp,
                        fontWeight =
                            FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                // I NEED HELP
                Button(
                    onClick = {
                        viewModel.activateEmergencyMode(
                            "Manual 'I NEED HELP' Triggered"
                        )
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                KavachEmergencyDarkRed,
                            contentColor =
                                Color.White
                        ),
                    shape =
                        RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(
                            1.dp,
                            Color.White.copy(
                                alpha = 0.25f
                            ),
                            RoundedCornerShape(16.dp)
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Warning,
                        contentDescription = null,
                        modifier =
                            Modifier.size(20.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text = "I NEED HELP",
                        fontSize = 15.sp,
                        fontWeight =
                            FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        // ─────────────────────────────
        // LOCATION TELEMETRY
        // ─────────────────────────────

        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        KavachDarkSurface
                ),
            shape =
                RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    KavachDarkCardBorder,
                    RoundedCornerShape(20.dp)
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(16.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                KavachSafeGreenBg,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.GpsFixed,
                            contentDescription =
                                "GPS",
                            tint =
                                KavachSafeGreen,
                            modifier =
                                Modifier.size(21.dp)
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Column {
                        Text(
                            text =
                                "ANOMALY LOCATION",
                            color =
                                KavachTextMuted,
                            fontSize = 10.sp,
                            fontWeight =
                                FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )

                        Text(
                            text =
                                "Live GPS position captured",
                            color =
                                KavachTextPrimary,
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(13.dp)
                )

                Text(
                    text = currentAddress,
                    color =
                        KavachTextPrimary,
                    fontSize = 13.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        currentCoordinates,
                    color =
                        KavachCyanPrimary,
                    fontSize = 11.sp,
                    fontFamily =
                        FontFamily.Monospace
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        // ─────────────────────────────
        // ESCALATION STATUS
        // ─────────────────────────────

        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        KavachEmergencyBg
                ),
            shape =
                RoundedCornerShape(18.dp),
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Warning,
                    contentDescription =
                        null,
                    tint =
                        KavachWarningAmber,
                    modifier =
                        Modifier.size(21.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "AUTONOMOUS ESCALATION",
                        color =
                            KavachWarningAmber,
                        fontSize = 10.sp,
                        fontWeight =
                            FontWeight.Bold,
                        letterSpacing =
                            0.8.sp
                    )

                    Text(
                        text =
                            "If you don't respond, your safety network will be activated.",
                        color =
                            KavachTextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }

                Text(
                    text =
                        "GPS + ALERT",
                    color =
                        KavachTextPrimary,
                    fontSize = 8.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(
            text =
                "ABHAYA KAVACH • YOUR SAFETY COMES FIRST",
            color =
                KavachTextDim,
            fontSize = 8.sp,
            fontWeight =
                FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign =
                TextAlign.Center,
            modifier =
                Modifier.fillMaxWidth()
        )

        Spacer(
            modifier =
                Modifier.height(10.dp)
        )
    }
}
