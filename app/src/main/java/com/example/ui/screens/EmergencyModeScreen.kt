package com.example.ui.screens
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachDarkSurfaceVariant
import com.example.ui.theme.KavachEmergencyBg
import com.example.ui.theme.KavachEmergencyDarkRed
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachLavender
import com.example.ui.theme.KavachLavenderBg
import com.example.ui.theme.KavachPowderBlueBg
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachSafeGreenBg
import com.example.ui.theme.KavachTextDim
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachTextSecondary
import com.example.ui.theme.KavachWarningAmber
import com.example.viewmodel.SafetyViewModel

@Composable
fun EmergencyModeScreen(
    viewModel: SafetyViewModel,
    modifier: Modifier = Modifier
) {
    val currentAddress by viewModel.currentAddress.collectAsState()
    val currentCoordinates by viewModel.currentCoordinates.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val dispatchedAlerts by viewModel.dispatchedAlerts.collectAsState()
    val isSirenActive by viewModel.isSirenActive.collectAsState()
    val activeJourney by viewModel.activeJourney.collectAsState()

    var showCancelDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    val infiniteTransition =
        rememberInfiniteTransition(label = "emergency_pulse")

    val emergencyPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                700,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emergency_pulse"
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
        // EMERGENCY HEADER
        // ─────────────────────────────

        Card(
            colors = CardDefaults.cardColors(
                containerColor = KavachEmergencyBg
            ),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    KavachEmergencyRed.copy(alpha = 0.45f),
                    RoundedCornerShape(26.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(86.dp)
                        .scale(emergencyPulse)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    KavachEmergencyRed.copy(
                                        alpha = 0.35f
                                    ),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .border(
                            2.dp,
                            KavachEmergencyRed,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Emergency",
                        tint = KavachEmergencyRed,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "EMERGENCY MODE",
                    color = KavachEmergencyRed,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Your safety network has been activated",
                    color = KavachTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(50.dp)
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                KavachSafeGreen,
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = "LIVE GPS TRANSMISSION ACTIVE",
                        color = KavachSafeGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ─────────────────────────────
        // GPS LOCATION
        // ─────────────────────────────

        SectionTitle("LIVE LOCATION")

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                KavachPowderBlueBg,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "GPS",
                            tint = KavachCyanPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(11.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "GPS LOCKED",
                            color = KavachSafeGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Current location",
                            color = KavachTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                KavachSafeGreenBg,
                                RoundedCornerShape(50.dp)
                            )
                            .padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            )
                    ) {
                        Text(
                            text = "LIVE",
                            color = KavachSafeGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(13.dp))

                Text(
                    text = currentAddress,
                    color = KavachTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentCoordinates,
                    color = KavachCyanPrimary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────
        // EMERGENCY CALLS
        // ─────────────────────────────

        SectionTitle("GET HELP NOW")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            EmergencyCallButton(
                modifier = Modifier.weight(1f),
                number = "112",
                label = "Police",
                icon = Icons.Default.LocalPolice,
                background = KavachEmergencyRed
            ) {
                try {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:112")
                        )
                    )
                } catch (_: Exception) {
                    viewModel.showToast(
                        "Opening dialer for 112"
                    )
                }
            }

            EmergencyCallButton(
                modifier = Modifier.weight(1f),
                number = "1091",
                label = "Women",
                icon = Icons.Default.PhoneInTalk,
                background = KavachEmergencyDarkRed
            ) {
                try {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:1091")
                        )
                    )
                } catch (_: Exception) {
                    viewModel.showToast(
                        "Opening dialer for 1091"
                    )
                }
            }
        }

        val primaryContact =
            contacts.firstOrNull { it.isPrimary }
                ?: contacts.firstOrNull()

        if (primaryContact != null) {

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    try {
                        val cleanPhone =
                            primaryContact.phone.replace(
                                " ",
                                ""
                            )

                        context.startActivity(
                            Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse("tel:$cleanPhone")
                            )
                        )
                    } catch (_: Exception) {
                        viewModel.showToast(
                            "Calling ${primaryContact.name}"
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = KavachCyanPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(
                        1.dp,
                        KavachCyanPrimary.copy(
                            alpha = 0.5f
                        ),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text =
                        "CALL ${primaryContact.name.uppercase()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────
        // GUARDIANS ALERT STATUS
        // ─────────────────────────────

        SectionTitle("GUARDIANS ALERTED")

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = KavachDarkSurface
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    KavachDarkCardBorder,
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
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
                                KavachSafeGreenBg,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = KavachSafeGreen,
                            modifier = Modifier.size(21.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(11.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text =
                                "${dispatchedAlerts.size} guardians",
                            color = KavachTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text =
                                "Emergency alert dispatched",
                            color = KavachTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "SENT",
                        color = KavachSafeGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (dispatchedAlerts.isNotEmpty()) {

                    Spacer(modifier = Modifier.height(12.dp))

                    dispatchedAlerts.forEach { alert ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 5.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        KavachSafeGreen,
                                        CircleShape
                                    )
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(9.dp)
                            )

                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {
                                Text(
                                    text =
                                        alert.recipientName,
                                    color =
                                        KavachTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight =
                                        FontWeight.SemiBold
                                )

                                Text(
                                    text =
                                        alert.recipientPhone,
                                    color =
                                        KavachTextMuted,
                                    fontSize = 9.sp
                                )
                            }

                            Text(
                                text = alert.status,
                                color = KavachSafeGreen,
                                fontSize = 9.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ─────────────────────────────
        // LIVE TRACKING LINK
        // ─────────────────────────────

        val liveTrackUrl =
            "https://abhayakavach.safe/track/${activeJourney?.id ?: "live-sos"}"

        val sosFullText =
            "SOS! I need help. My current GPS: $currentCoordinates ($currentAddress). Live tracking link: $liveTrackUrl"

        Card(
            colors = CardDefaults.cardColors(
                containerColor = KavachPowderBlueBg
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
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
                                Color.White.copy(
                                    alpha = 0.8f
                                ),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.Share,
                            contentDescription =
                                null,
                            tint =
                                KavachCyanPrimary,
                            modifier =
                                Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier =
                        Modifier.width(10.dp))

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {
                        Text(
                            text =
                                "LIVE SOS TRACKING",
                            color =
                                KavachTextPrimary,
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Text(
                            text =
                                "Share your location with anyone",
                            color =
                                KavachTextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    Text(
                        text = "COPY",
                        color =
                            KavachCyanPrimary,
                        fontSize = 9.sp,
                        fontWeight =
                            FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                clipboardManager
                                    .setText(
                                        AnnotatedString(
                                            sosFullText
                                        )
                                    )

                                viewModel.showToast(
                                    "SOS Message & Live Link copied"
                                )
                            }
                            .padding(5.dp)
                    )
                }

                Spacer(modifier =
                    Modifier.height(10.dp))

                Text(
                    text = liveTrackUrl,
                    color = KavachCyanPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ─────────────────────────────
        // SIREN
        // ─────────────────────────────

        Button(
            onClick = {
                viewModel.toggleSiren()

                viewModel.showToast(
                    if (!isSirenActive)
                        "Siren & Strobe Activated"
                    else
                        "Siren Muted"
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor =
                    if (isSirenActive)
                        KavachWarningAmber
                    else
                        KavachDarkSurface,
                contentColor =
                    if (isSirenActive)
                        KavachDarkBg
                    else
                        KavachTextPrimary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .border(
                    1.dp,
                    if (isSirenActive)
                        KavachWarningAmber
                    else
                        KavachDarkCardBorder,
                    RoundedCornerShape(16.dp)
                )
        ) {

            Icon(
                imageVector =
                    if (isSirenActive)
                        Icons.Default.VolumeUp
                    else
                        Icons.Default.FlashOn,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
            )

            Spacer(modifier =
                Modifier.width(8.dp))

            Text(
                text =
                    if (isSirenActive)
                        "MUTE SIREN & STROBE"
                    else
                        "ACTIVATE LOUD SIREN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // ─────────────────────────────
        // DEACTIVATE
        // ─────────────────────────────

        OutlinedButton(
            onClick = {
                showCancelDialog = true
            },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = KavachSafeGreen
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = KavachSafeGreen,
                modifier = Modifier.size(19.dp)
            )

            Spacer(modifier =
                Modifier.width(8.dp))

            Text(
                text = "I'M SAFE — DEACTIVATE EMERGENCY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text =
                "ABHAYA KAVACH • HELP IS WITHIN REACH",
            color = KavachTextDim,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier =
                Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(10.dp))
    }

    // ─────────────────────────────
    // CONFIRMATION DIALOG
    // ─────────────────────────────

    if (showCancelDialog) {

        AlertDialog(
            onDismissRequest = {
                showCancelDialog = false
            },

            title = {
                Text(
                    text = "Deactivate Emergency Mode?",
                    color = KavachTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text =
                        "Are you sure you are safe? This will stop live beacon transmission and notify your emergency contacts that the emergency has ended.",
                    color = KavachTextSecondary,
                    fontSize = 13.sp
                )
            },

            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelEmergency()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KavachSafeGreen,
                        contentColor = KavachDarkBg
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "YES, I'M SAFE",
                        fontWeight = FontWeight.Bold
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                    }
                ) {
                    Text(
                        text = "KEEP ACTIVE",
                        color = KavachTextMuted
                    )
                }
            },

            containerColor = KavachDarkSurface,
            shape = RoundedCornerShape(22.dp)
        )
    }
}

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        color = KavachTextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.4.sp
    )
}

@Composable
private fun EmergencyCallButton(
    modifier: Modifier,
    number: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(56.dp)
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier =
                    Modifier.width(5.dp))

                Text(
                    text = number,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
