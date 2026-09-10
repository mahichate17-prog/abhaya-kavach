package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RealOsmMapVisualizer
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachDarkSurfaceVariant
import com.example.ui.theme.KavachEmergencyBg
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachPowderBlue
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachSafeGreenBg
import com.example.ui.theme.KavachTextDim
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachTextSecondary
import com.example.ui.theme.KavachWarningAmber
import com.example.viewmodel.SafetyViewModel

@Composable
fun JourneyMonitoringScreen(
    viewModel: SafetyViewModel,
    modifier: Modifier = Modifier
) {
    val activeJourney by viewModel.activeJourney.collectAsState()
    val plannedRoute by viewModel.plannedRoute.collectAsState()
    val isRouteLoading by viewModel.isRouteLoading.collectAsState()
    val journeyProgress by viewModel.journeyProgress.collectAsState()
    val currentSpeed by viewModel.currentSpeedKmh.collectAsState()
    val currentAddress by viewModel.currentAddress.collectAsState()
    val currentCoordinates by viewModel.currentCoordinates.collectAsState()
    val isDeviating by viewModel.isDeviating.collectAsState()
    val isSimulationMode by viewModel.isSimulationMode.collectAsState()
    val isRealGpsActive by viewModel.isRealGpsActive.collectAsState()
    val isLocationPermissionGranted by viewModel.isLocationPermissionGranted.collectAsState()
    val isLocationServiceEnabled by viewModel.isLocationServiceEnabled.collectAsState()
    val locationErrorMessage by viewModel.locationErrorMessage.collectAsState()
    val locationAccuracy by viewModel.locationAccuracyMeters.collectAsState()
    val rawLat by viewModel.rawLatitude.collectAsState()
    val rawLon by viewModel.rawLongitude.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            viewModel.onPermissionResult(
                fineGranted || coarseGranted
            )
        }

    val infiniteTransition =
        rememberInfiniteTransition(label = "safe_status_pulse")

    val dotPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KavachDarkBg)
            .verticalScroll(scrollState)
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
    ) {

        // ─────────────────────────────
        // HEADER
        // ─────────────────────────────

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
                    text = if (isSimulationMode)
                        "SIMULATION / ANOMALY TEST MODE"
                    else
                        "LIVE GPS ACTIVE MONITORING",
                    color = if (isSimulationMode)
                        KavachWarningAmber
                    else
                        KavachSafeGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        if (
                            isRealGpsActive &&
                            isLocationPermissionGranted
                        )
                            KavachSafeGreenBg
                        else
                            KavachEmergencyBg,
                        RoundedCornerShape(50.dp)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    )
            ) {
                Text(
                    text = if (isSimulationMode)
                        "DEMO"
                    else if (isRealGpsActive)
                        "● LIVE GPS"
                    else
                        "GPS PENDING",
                    color = if (isSimulationMode)
                        KavachWarningAmber
                    else if (isRealGpsActive)
                        KavachSafeGreen
                    else
                        KavachWarningAmber,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ─────────────────────────────
        // DISCREET SOS
        // ─────────────────────────────

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    KavachEmergencyBg,
                    RoundedCornerShape(14.dp)
                )
                .border(
                    1.dp,
                    KavachEmergencyRed.copy(alpha = 0.25f),
                    RoundedCornerShape(14.dp)
                )
                .clickable {
                    viewModel.activateEmergencyMode(
                        "Discreet SOS Button Pressed"
                    )
                }
                .padding(
                    horizontal = 14.dp,
                    vertical = 11.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "SOS",
                    tint = KavachEmergencyRed,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "DISCREET SOS",
                    color = KavachEmergencyRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ─────────────────────────────
        // GPS WARNING
        // ─────────────────────────────

        if (
            !isLocationPermissionGranted ||
            !isLocationServiceEnabled ||
            locationErrorMessage != null
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = KavachDarkSurfaceVariant
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        KavachWarningAmber,
                        RoundedCornerShape(14.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsOff,
                            contentDescription = "GPS Warning",
                            tint = KavachWarningAmber,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text =
                                if (!isLocationPermissionGranted)
                                    "LOCATION PERMISSION REQUIRED"
                                else
                                    "GPS DISABLED",
                            color = KavachWarningAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = locationErrorMessage
                            ?: if (!isLocationPermissionGranted)
                                "Real-time location tracking is unavailable without location permission."
                            else
                                "Device location service is turned off. Please enable Location in system settings.",
                        color = KavachTextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        if (!isLocationPermissionGranted) {
                            Button(
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = KavachCyanPrimary,
                                    contentColor = KavachDarkBg
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    "GRANT PERMISSION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (!isLocationServiceEnabled) {
                            Button(
                                onClick = {
                                    try {
                                        context.startActivity(
                                            Intent(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                            )
                                        )
                                    } catch (_: Exception) {
                                        viewModel.showToast(
                                            "Enable Location in Settings"
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = KavachWarningAmber,
                                    contentColor = KavachDarkBg
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    "ENABLE GPS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.startTrackingRealGps()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Retry",
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                "RETRY GPS",
                                fontSize = 11.sp,
                                color = KavachTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // ─────────────────────────────
        // TRACKING CARD
        // ─────────────────────────────

        Card(
            colors = CardDefaults.cardColors(
                containerColor = KavachDarkSurface
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    KavachDarkCardBorder,
                    RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ACTIVE JOURNEY",
                            color = KavachTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Your route is protected",
                            color = KavachTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = KavachSafeGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // REAL OSM MAP — FUNCTIONALITY UNTOUCHED
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(22.dp)
                        )
                        .border(
                            1.dp,
                            KavachPowderBlue.copy(
                                alpha = 0.35f
                            ),
                            RoundedCornerShape(22.dp)
                        )
                ) {
                    RealOsmMapVisualizer(
                        journey = activeJourney,
                        plannedRoute = plannedRoute,
                        userLat = rawLat,
                        userLon = rawLon,
                        userSpeedKmh = currentSpeed,
                        locationAccuracyMeters =
                            locationAccuracy,
                        isDeviating = isDeviating,
                        isRouteLoading = isRouteLoading
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // JOURNEY STATUS
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDeviating)
                            KavachEmergencyBg
                        else
                            KavachSafeGreenBg
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment =
                                Alignment.Center,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .scale(dotPulse)
                                    .background(
                                        (if (isDeviating)
                                            KavachEmergencyRed
                                        else
                                            KavachSafeGreen
                                        ).copy(
                                            alpha = 0.18f
                                        ),
                                        CircleShape
                                    )
                            )

                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        if (isDeviating)
                                            KavachEmergencyRed
                                        else
                                            KavachSafeGreen,
                                        CircleShape
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isDeviating)
                                    "Something changed"
                                else
                                    "Journey is safe",
                                color = if (isDeviating)
                                    KavachEmergencyRed
                                else
                                    KavachSafeGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (isDeviating)
                                    "Route deviation detected"
                                else
                                    "You're following your protected route",
                                color = KavachTextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        if (locationAccuracy != null) {
                            Column(
                                horizontalAlignment =
                                    Alignment.End
                            ) {
                                Text(
                                    text =
                                        "±${locationAccuracy?.toInt()}m",
                                    color =
                                        KavachCyanPrimary,
                                    fontSize = 13.sp,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text = "GPS ACCURACY",
                                    color =
                                        KavachTextMuted,
                                    fontSize = 8.sp,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = {
                        if (isRealGpsActive)
                            0.65f
                        else
                            journeyProgress
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(
                            RoundedCornerShape(3.dp)
                        ),
                    color = KavachCyanPrimary,
                    trackColor =
                        KavachDarkSurfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ─────────────────────────────
        // TELEMETRY
        // ─────────────────────────────

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            TelemetryGridCard(
                title = "SPEED",
                value = "$currentSpeed KM/H",
                modifier = Modifier.weight(1f)
            )

            TelemetryGridCard(
                title = "ACCURACY",
                value = if (locationAccuracy != null)
                    "±${locationAccuracy?.toInt()} M"
                else
                    "GPS LOCK",
                modifier = Modifier.weight(1f)
            )

            TelemetryGridCard(
                title = "TRACKER",
                value = if (isRealGpsActive)
                    "FUSED GPS"
                else
                    "INITIALIZING",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ─────────────────────────────
        // LOCATION CARD
        // ─────────────────────────────

        Card(
            colors = CardDefaults.cardColors(
                containerColor = KavachDarkSurface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    KavachDarkCardBorder,
                    RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    imageVector =
                        if (isRealGpsActive)
                            Icons.Default.GpsFixed
                        else
                            Icons.Default.LocationOn,
                    contentDescription = null,
                    tint =
                        if (isRealGpsActive)
                            KavachSafeGreen
                        else
                            KavachCyanPrimary,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = currentAddress,
                        color = KavachTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text =
                            "GPS: $currentCoordinates",
                        color =
                            if (isRealGpsActive)
                                KavachCyanPrimary
                            else
                                KavachTextMuted,
                        fontSize = 11.sp,
                        fontFamily =
                            FontFamily.Monospace,
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    if (rawLat != null && rawLon != null) {
                        Text(
                            text = String.format(
                                java.util.Locale.US,
                                "Raw: %.6f, %.6f",
                                rawLat,
                                rawLon
                            ),
                            color = KavachTextMuted,
                            fontSize = 9.sp,
                            fontFamily =
                                FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        
        
        Spacer(modifier = Modifier.height(6.dp))
        // ─────────────────────────────
// LIVE PROACTIVE DETECTION
// ─────────────────────────────

Card(
    colors = CardDefaults.cardColors(
        containerColor = KavachDarkSurface
    ),
    shape = RoundedCornerShape(14.dp),
    modifier = Modifier
        .fillMaxWidth()
        .border(
            1.dp,
            KavachSafeGreen.copy(alpha = 0.35f),
            RoundedCornerShape(14.dp)
        )
) {
    Column(
        modifier = Modifier.padding(14.dp)
    ) {
        Text(
            text = "LIVE PROACTIVE DETECTION",
            color = KavachSafeGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Route deviation  > 30m  •  2 GPS fixes\n" +
                    "Suspicious halt  < 3 km/h  •  30s",
            color = KavachTextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "GPS accuracy gate: ≤ 50m",
            color = KavachTextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

       
        Spacer(modifier = Modifier.height(16.dp))

        // ─────────────────────────────
        // END JOURNEY
        // ─────────────────────────────

        Button(
            onClick = {
                viewModel.endJourneySafe()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = KavachSafeGreen,
                contentColor = KavachDarkBg
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text =
                        "END JOURNEY • ARRIVED SAFELY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.7.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ABHAYA KAVACH • JOURNEY PROTECTED",
            color = KavachTextDim,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun TelemetryGridCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = KavachDarkSurface
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.border(
            1.dp,
            KavachDarkCardBorder,
            RoundedCornerShape(14.dp)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                color = KavachTextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                color = KavachTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
