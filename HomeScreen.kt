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
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneInTalk
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.ui.components.PulsingShield
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachDarkSurfaceVariant
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachEmergencyBg
import com.example.ui.theme.KavachLavender
import com.example.ui.theme.KavachLavenderBg
import com.example.ui.theme.KavachPowderBlue
import com.example.ui.theme.KavachPowderBlueBg
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachSafeGreenBg
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
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {

        // ─────────────────────────────────────
        // HEADER
        // ─────────────────────────────────────

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(KavachLavenderBg, CircleShape)
                        .border(
                            1.dp,
                            KavachLavender.copy(alpha = 0.35f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = KavachLavender,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Abhaya Kavach",
                        color = KavachTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Your safety companion",
                        color = KavachTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Protected indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        KavachSafeGreenBg,
                        RoundedCornerShape(50.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(KavachSafeGreen, CircleShape)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "SAFE",
                    color = KavachSafeGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ─────────────────────────────────────
        // HERO SAFETY CARD
        // ─────────────────────────────────────

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    KavachDarkCardBorder,
                    RoundedCornerShape(28.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Small label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            KavachLavenderBg,
                            RoundedCornerShape(50.dp)
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = KavachLavender,
                        modifier = Modifier.size(15.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "PROTECTION ACTIVE",
                        color = KavachLavender,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (activeJourney != null)
                        "You're on a journey"
                    else
                        "You're safe",
                    color = KavachTextPrimary,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (activeJourney != null)
                        "Abhaya Kavach is watching over you"
                    else
                        "Ready to keep you protected",
                    color = KavachTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Existing shield
                PulsingShield(
                    status = safetyStatus,
                    size = 118.dp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Start / Resume Journey
                Button(
                    onClick = {
                        if (activeJourney != null) {
                            viewModel.navigateTo(
                                AppScreen.JOURNEY_MONITORING
                            )
                        } else {
                            viewModel.navigateTo(
                                AppScreen.JOURNEY_SETUP
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KavachCyanPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (activeJourney != null)
                            "RESUME JOURNEY"
                        else
                            "START A JOURNEY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─────────────────────────────────────
        // STATUS SECTION
        // ─────────────────────────────────────

        Text(
            text = "Your protection",
            color = KavachTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // GPS
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.GpsFixed,
                title = "Location",
                value = if (isLocationGranted)
                    "Connected"
                else
                    "Standby",
                iconBackground = KavachPowderBlueBg,
                iconTint = KavachPowderBlue,
                valueColor = if (isLocationGranted)
                    KavachSafeGreen
                else
                    KavachTextMuted
            )

            // Contacts
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.People,
                title = "Guardians",
                value = "${contacts.size} Active",
                iconBackground = KavachLavenderBg,
                iconTint = KavachLavender,
                valueColor = KavachTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Route Radar
        Card(
            colors = CardDefaults.cardColors(
                containerColor = KavachPowderBlueBg
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color.White.copy(alpha = 0.75f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = KavachPowderBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Route Radar",
                        color = KavachTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Monitoring your journey",
                        color = KavachTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "ARMED",
                    color = KavachSafeGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────────────
        // QUICK ACTIONS
        // ─────────────────────────────────────

        Text(
            text = "Quick actions",
            color = KavachTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Guardians
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.ContactPhone,
                title = "Trusted",
                subtitle = "Guardians",
                iconBackground = KavachLavenderBg,
                iconTint = KavachLavender,
                onClick = {
                    viewModel.navigateTo(
                        AppScreen.EMERGENCY_CONTACTS
                    )
                }
            )

            // SOS
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Warning,
                title = "Emergency",
                subtitle = "SOS",
                iconBackground = KavachEmergencyBg,
                iconTint = KavachEmergencyRed,
                onClick = {
                    viewModel.activateEmergencyMode(
                        "Instant SOS Trigger"
                    )
                },
                danger = true
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // ─────────────────────────────────────
        // REASSURANCE CARD
        // ─────────────────────────────────────

        Card(
            colors = CardDefaults.cardColors(
                containerColor = KavachLavenderBg
            ),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.8f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = KavachLavender,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "We've got you covered",
                        color = KavachTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Your safety network is ready whenever you need it.",
                        color = KavachTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Footer
        Text(
            text = "ABHAYA KAVACH  •  SAFETY, WITH YOU",
            color = KavachTextDim,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun StatusCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    iconBackground: Color,
    iconTint: Color,
    valueColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        iconBackground,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                color = KavachTextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                color = valueColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit,
    danger: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                1.dp,
                if (danger)
                    KavachEmergencyRed.copy(alpha = 0.25f)
                else
                    KavachDarkCardBorder,
                RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        iconBackground,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = if (danger)
                    KavachEmergencyRed
                else
                    KavachTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = KavachTextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
