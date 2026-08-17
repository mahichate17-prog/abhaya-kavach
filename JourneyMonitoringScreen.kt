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
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
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
import com.example.model.AppScreen
import com.example.model.SafetyStatus
import com.example.ui.components.InteractiveRouteVisualizer
import com.example.ui.components.PulsingShield
import com.example.ui.components.RealOsmMapVisualizer
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachDarkSurfaceVariant
import com.example.ui.theme.KavachEmergencyBg
import com.example.ui.theme.KavachEmergencyRed
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val granted = fineGranted || coarseGranted
        viewModel.onPermissionResult(granted)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "safe_status_pulse")
    val dotPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_pulse"
    )
Column(
    modifier = modifier
        .fillMaxSize()
        .background(KavachDarkBg)
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 16.dp)
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
                    text = if (isSimulationMode) "SIMULATION / ANOMALY TEST MODE" else "LIVE GPS ACTIVE MONITORING",
                    color = if (isSimulationMode) KavachWarningAmber else KavachSafeGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Discreet Quick SOS Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(KavachEmergencyBg)
                    .border(1.dp, KavachEmergencyRed, RoundedCornerShape(8.dp))
                    .clickable { viewModel.activateEmergencyMode("Discreet SOS Button Pressed") }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SOS",
                        tint = KavachEmergencyRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "DISCREET SOS",
                        color = KavachEmergencyRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GPS Permission or Location Service Error Warning Banner (if error)
        if (!isLocationPermissionGranted || !isLocationServiceEnabled || locationErrorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = KavachDarkSurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KavachWarningAmber, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GpsOff,
                            contentDescription = "GPS Warning",
                            tint = KavachWarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (!isLocationPermissionGranted) "LOCATION PERMISSION REQUIRED" else "GPS DISABLED",
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
                                "Real-time location tracking is unavailable without ACCESS_FINE_LOCATION permission."
                            else
                                "Device location service is turned off. Please turn on Location in system settings.",
                        color = KavachTextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                Text("GRANT PERMISSION", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (!isLocationServiceEnabled) {
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        viewModel.showToast("Enable Location in Settings")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = KavachWarningAmber,
                                    contentColor = KavachDarkBg
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("ENABLE GPS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        OutlinedButton(
                            onClick = { viewModel.startTrackingRealGps() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RETRY GPS", fontSize = 11.sp, color = KavachTextPrimary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Screen 02 // Tracking Main Container
        Card(
            colors = CardDefaults.cardColors(containerColor = KavachDarkSurface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KavachDarkCardBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Section Tag & Headline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCREEN 02 // TRACKING",
                        color = KavachTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    // LIVE GPS Chip
                    Box(
                        modifier = Modifier
                            .background(
                                if (isRealGpsActive && isLocationPermissionGranted)
                                    KavachSafeGreen.copy(alpha = 0.15f)
                                else
                                    KavachWarningAmber.copy(alpha = 0.15f),
                                RoundedCornerShape(6.dp)
                            )
                            .border(
                                0.5.dp,
                                if (isRealGpsActive && isLocationPermissionGranted) KavachSafeGreen else KavachWarningAmber,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isSimulationMode) "SIMULATION" else if (isRealGpsActive) "LIVE GPS" else "GPS PENDING",
                            color = if (isSimulationMode) KavachWarningAmber else if (isRealGpsActive) KavachSafeGreen else KavachWarningAmber,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "ACTIVE\nROUTE",
                    color = KavachTextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 26.sp,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Real Interactive OpenStreetMap with Decoded Road Route Polyline & Live GPS
                RealOsmMapVisualizer(
                    journey = activeJourney,
                    plannedRoute = plannedRoute,
                    userLat = rawLat,
                    userLon = rawLon,
                    userSpeedKmh = currentSpeed,
                    locationAccuracyMeters = locationAccuracy,
                    isDeviating = isDeviating,
                    isRouteLoading = isRouteLoading
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Status & Progress Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(dotPulse)
                                    .background(
                                        (if (isDeviating) KavachWarningAmber else KavachSafeGreen).copy(alpha = 0.3f),
                                        CircleShape
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (isDeviating) KavachWarningAmber else KavachSafeGreen,
                                        CircleShape
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDeviating) "ANOMALY DETECTED" else "JOURNEY SAFE",
                            color = if (isDeviating) KavachWarningAmber else KavachSafeGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = if (locationAccuracy != null) "±${locationAccuracy?.toInt()}m ACCURACY" else "HIGH PRECISION",
                        color = KavachCyanPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { if (isRealGpsActive) 0.65f else journeyProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = KavachCyanPrimary,
                    trackColor = KavachDarkSurfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Telemetry 3-Grid (Displaying Real Speed & Location Data)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryGridCard(
                title = "SPEED",
                value = "$currentSpeed KM/H",
                modifier = Modifier.weight(1f)
            )
            TelemetryGridCard(
                title = "ACCURACY",
                value = if (locationAccuracy != null) "±${locationAccuracy?.toInt()} M" else "GPS LOCK",
                modifier = Modifier.weight(1f)
            )
            TelemetryGridCard(
                title = "TRACKER",
                value = if (isRealGpsActive) "FUSED GPS" else "INITIALIZING",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Real GPS Address & Coordinates Card
        Card(
            colors = CardDefaults.cardColors(containerColor = KavachDarkSurface),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, KavachDarkCardBorder, RoundedCornerShape(14.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isRealGpsActive) Icons.Default.GpsFixed else Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (isRealGpsActive) KavachSafeGreen else KavachCyanPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentAddress,
                        color = KavachTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "GPS: $currentCoordinates",
                        color = if (isRealGpsActive) KavachCyanPrimary else KavachTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (rawLat != null && rawLon != null) {
                        Text(
                            text = "Raw: ${String.format(java.util.Locale.US, "%.6f, %.6f", rawLat, rawLon)}",
                            color = KavachTextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Anomaly Simulation Controls (Kept as demo features with clear labeling)
        Text(
            text = "PROACTIVE DETECTION TEST LAB (DEMO)",
            color = KavachTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.triggerRouteDeviationAnomaly() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = KavachDarkSurfaceVariant,
                    contentColor = KavachWarningAmber
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .border(1.dp, KavachWarningAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "SIMULATE DEVIATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Button(
                onClick = { viewModel.triggerProlongedHaltAnomaly() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = KavachDarkSurfaceVariant,
                    contentColor = KavachWarningAmber
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .border(1.dp, KavachWarningAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "SIMULATE LONG HALT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // End Journey Safe Button
        Button(
            onClick = { viewModel.endJourneySafe() },
            colors = ButtonDefaults.buttonColors(
                containerColor = KavachSafeGreen,
                contentColor = KavachDarkBg
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "END JOURNEY (ARRIVED SAFELY)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun TelemetryGridCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = KavachDarkSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.border(1.dp, KavachDarkCardBorder, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
