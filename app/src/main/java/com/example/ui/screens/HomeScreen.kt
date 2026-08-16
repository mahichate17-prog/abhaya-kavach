package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.ui.components.PulsingShield
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkCardBorderBright
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachDarkSurfaceVariant
import com.example.ui.theme.KavachEmergencyBg
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachSafeGreenBg
import com.example.ui.theme.KavachSafeGreenGlow
import com.example.ui.theme.KavachTextDim
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachTextSecondary
import com.example.viewmodel.SafetyViewModel

@Composable
fun HomeScreen(
    viewModel: SafetyViewModel,
    modifier: Modifier = Modifier
) {
    val safetyStatus by viewModel.safetyStatus.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val isLocationGranted by viewModel.isLocationPermissionGranted.collectAsState()
    val activeJourney by viewModel.activeJourney.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KavachDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Top Header matching Bold Typography Theme
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.dp, Color.Transparent)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "ABHAYA\nKAVACH",
                    color = KavachTextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 30.sp,
                    letterSpacing = (-1.5).sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "YOUR JOURNEY. YOUR SAFETY. ALWAYS.",
                    color = KavachTextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                // System Protected pill with glowing emerald indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(KavachDarkSurface, RoundedCornerShape(20.dp))
                        .border(1.dp, KavachDarkCardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(KavachSafeGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SYSTEM PROTECTED",
                        color = KavachSafeGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "V4.0.2 // SECURE CORE",
                    color = KavachTextDim,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Horizontal divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(KavachDarkCardBorder)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Screen 01 // Home Card
        Card(
            colors = CardDefaults.cardColors(containerColor = KavachDarkSurface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KavachDarkCardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Screen 01 Tag & Safe Entry Headline
                Text(
                    text = "SCREEN 01 // HOME",
                    color = KavachTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "SAFE\nENTRY",
                    color = KavachTextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 26.sp,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Center Pulsing Shield & Start Button Group
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PulsingShield(
                        status = safetyStatus,
                        size = 110.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Circular / Rounded Action Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(116.dp)
                            .background(Color.Transparent, CircleShape)
                            .border(2.dp, KavachDarkCardBorder, CircleShape)
                            .padding(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (activeJourney != null) {
                                    viewModel.navigateTo(AppScreen.JOURNEY_MONITORING)
                                } else {
                                    viewModel.navigateTo(AppScreen.JOURNEY_SETUP)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KavachSafeGreen,
                                contentColor = KavachDarkBg
                            ),
                            shape = CircleShape,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (activeJourney != null) "RESUME" else "START",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    lineHeight = 13.sp
                                )
                                Text(
                                    text = "JOURNEY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Status Mini Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KavachDarkSurfaceVariant, RoundedCornerShape(12.dp))
                            .border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTACTS",
                            color = KavachTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = String.format("%02d ACTIVE", contacts.size),
                            color = KavachTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KavachDarkSurfaceVariant, RoundedCornerShape(12.dp))
                            .border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GPS STATUS",
                            color = KavachTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isLocationGranted) "LOCKED" else "STANDBY",
                            color = if (isLocationGranted) KavachSafeGreen else KavachTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KavachDarkSurfaceVariant, RoundedCornerShape(12.dp))
                            .border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI ROUTE RADAR",
                            color = KavachTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "ARMED",
                            color = KavachSafeGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Navigation Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Emergency Contacts Button
            Card(
                colors = CardDefaults.cardColors(containerColor = KavachDarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, KavachDarkCardBorder, RoundedCornerShape(16.dp))
                    .clickable { viewModel.navigateTo(AppScreen.EMERGENCY_CONTACTS) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GUARDIANS",
                        color = KavachTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "EMERGENCY\nCONTACTS",
                        color = KavachTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 15.sp
                    )
                }
            }

            // Instant SOS Card
            Card(
                colors = CardDefaults.cardColors(containerColor = KavachDarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, KavachEmergencyRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.activateEmergencyMode("Instant SOS Trigger") }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "HIGH ALERT",
                        color = KavachEmergencyRed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "INSTANT\nSOS DISPATCH",
                        color = KavachEmergencyRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Footer Section matching Theme
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(KavachDarkCardBorder)
        )
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROACTIVE ENGINE",
                    color = KavachTextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Anomaly Detection v1.2",
                    color = KavachTextDim,
                    fontSize = 9.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "DATA PRIVACY",
                    color = KavachTextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "End-to-End Encrypted",
                    color = KavachTextDim,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "2024 SAFETY PROTOCOL INTERFACE",
            color = KavachTextDim,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

