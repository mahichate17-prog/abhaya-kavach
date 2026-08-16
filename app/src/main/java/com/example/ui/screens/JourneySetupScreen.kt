package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.model.TravelMode
import com.example.ui.theme.KavachCyanHover
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachDarkSurfaceVariant
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachTextSecondary
import com.example.ui.theme.KavachWarningAmber
import com.example.viewmodel.SafetyViewModel

@Composable
fun JourneySetupScreen(
    viewModel: SafetyViewModel,
    modifier: Modifier = Modifier
) {
    var startLocation by remember { mutableStateOf("MG Road Metro Station, Gate 2") }
    var destination by remember { mutableStateOf("Whitefield Tech Park, Block B") }
    var selectedMode by remember { mutableStateOf(TravelMode.CAB) }

    val isLocationGranted by viewModel.isLocationPermissionGranted.collectAsState()
    val isLocationServiceEnabled by viewModel.isLocationServiceEnabled.collectAsState()
    val currentAddress by viewModel.currentAddress.collectAsState()
    val currentCoordinates by viewModel.currentCoordinates.collectAsState()
    val locationErrorMessage by viewModel.locationErrorMessage.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Runtime Permission Request Launcher for ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val granted = fineGranted || coarseGranted
        viewModel.onPermissionResult(granted)
        if (granted) {
            viewModel.showToast("GPS Location Permission Granted")
        } else {
            viewModel.showToast("Location permission is required for real-time tracking")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KavachDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Navigation Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = Modifier
                    .size(40.dp)
                    .background(KavachDarkSurface, CircleShape)
                    .border(1.dp, KavachDarkCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = KavachTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Configure Journey",
                    color = KavachTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Establish safe corridor & real GPS monitoring",
                    color = KavachTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // GPS / Location Permission Requirement Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isLocationGranted && isLocationServiceEnabled) KavachDarkSurface else KavachDarkSurfaceVariant
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (isLocationGranted && isLocationServiceEnabled) KavachSafeGreen.copy(alpha = 0.4f) else KavachWarningAmber.copy(alpha = 0.6f),
                    RoundedCornerShape(14.dp)
                )
                .clickable {
                    if (!isLocationGranted) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    } else if (!isLocationServiceEnabled) {
                        try {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            viewModel.showToast("Please enable Location in device settings")
                        }
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (isLocationGranted && isLocationServiceEnabled)
                                    KavachSafeGreen.copy(alpha = 0.15f)
                                else
                                    KavachWarningAmber.copy(alpha = 0.15f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isLocationGranted && isLocationServiceEnabled) Icons.Default.GpsFixed else Icons.Default.Warning,
                            contentDescription = "GPS status",
                            tint = if (isLocationGranted && isLocationServiceEnabled) KavachSafeGreen else KavachWarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Real Device GPS Tracking",
                            color = KavachTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when {
                                !isLocationGranted -> "Permission Required • Tap to Grant"
                                !isLocationServiceEnabled -> "Device GPS Disabled • Tap to Turn On"
                                else -> "Active • FusedLocationProvider High Accuracy"
                            },
                            color = if (isLocationGranted && isLocationServiceEnabled) KavachSafeGreen else KavachWarningAmber,
                            fontSize = 11.sp
                        )
                    }
                }
                Switch(
                    checked = isLocationGranted,
                    onCheckedChange = { checked ->
                        if (checked && !isLocationGranted) {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        } else {
                            viewModel.setLocationPermission(checked)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = KavachSafeGreen,
                        checkedTrackColor = KavachDarkSurfaceVariant,
                        uncheckedThumbColor = KavachTextMuted,
                        uncheckedTrackColor = KavachDarkBg
                    )
                )
            }
        }

        // Show Error / Guidance Message if Location is not configured
        if (!isLocationGranted || !isLocationServiceEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (!isLocationGranted)
                    "⚠️ Location permission is required to detect route deviations and provide real-time coordinates."
                else
                    "⚠️ Device location services are turned off. Please enable GPS in device settings.",
                color = KavachWarningAmber,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Starting Location Field
        Text(
            text = "STARTING LOCATION",
            color = KavachTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = startLocation,
            onValueChange = { startLocation = it },
            placeholder = { Text("Enter pick up location", color = KavachTextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    tint = KavachSafeGreen
                )
            },
            trailingIcon = {
                Text(
                    text = "GPS",
                    color = KavachCyanPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            if (!isLocationGranted) {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                startLocation = if (currentAddress.isNotBlank() && !currentAddress.contains("Acquiring")) {
                                    currentAddress
                                } else {
                                    "Current GPS Location ($currentCoordinates)"
                                }
                            }
                        }
                        .background(KavachCyanPrimary.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = KavachDarkSurface,
                unfocusedContainerColor = KavachDarkSurface,
                focusedBorderColor = KavachCyanPrimary,
                unfocusedBorderColor = KavachDarkCardBorder,
                focusedTextColor = KavachTextPrimary,
                unfocusedTextColor = KavachTextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Starting preset suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetChip(label = "📍 Current GPS") {
                if (!isLocationGranted) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    startLocation = "Current GPS Location ($currentCoordinates)"
                }
            }
            PresetChip(label = "🏢 Office") { startLocation = "Embassy TechVillage Main Gate" }
            PresetChip(label = "🚇 Metro") { startLocation = "Indiranagar Metro Station" }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Destination Field
        Text(
            text = "DESTINATION",
            color = KavachTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            placeholder = { Text("Enter destination address", color = KavachTextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = KavachEmergencyRed
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = KavachDarkSurface,
                unfocusedContainerColor = KavachDarkSurface,
                focusedBorderColor = KavachCyanPrimary,
                unfocusedBorderColor = KavachDarkCardBorder,
                focusedTextColor = KavachTextPrimary,
                unfocusedTextColor = KavachTextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Destination preset suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetChip(label = "🏠 Home") { destination = "Green Glen Layout, Flat 402" }
            PresetChip(label = "🏢 Tech Park") { destination = "Whitefield Tech Park, Block B" }
            PresetChip(label = "🛍️ Mall") { destination = "Phoenix Marketcity Main Entry" }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Travel Mode Selector
        Text(
            text = "TRAVEL MODE",
            color = KavachTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TravelModeCard(
                mode = TravelMode.CAB,
                isSelected = selectedMode == TravelMode.CAB,
                modifier = Modifier.weight(1f)
            ) { selectedMode = TravelMode.CAB }

            TravelModeCard(
                mode = TravelMode.AUTO,
                isSelected = selectedMode == TravelMode.AUTO,
                modifier = Modifier.weight(1f)
            ) { selectedMode = TravelMode.AUTO }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TravelModeCard(
                mode = TravelMode.WALKING,
                isSelected = selectedMode == TravelMode.WALKING,
                modifier = Modifier.weight(1f)
            ) { selectedMode = TravelMode.WALKING }

            TravelModeCard(
                mode = TravelMode.TRANSIT,
                isSelected = selectedMode == TravelMode.TRANSIT,
                modifier = Modifier.weight(1f)
            ) { selectedMode = TravelMode.TRANSIT }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Estimated Safe Corridor Summary
        Card(
            colors = CardDefaults.cardColors(containerColor = KavachDarkSurfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Estimated Safe Route", color = KavachTextSecondary, fontSize = 11.sp)
                    Text(text = "8.4 km • ~22 mins", color = KavachTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Safe Score",
                        tint = KavachSafeGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Safety Score: 98%", color = KavachSafeGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "START JOURNEY" Button
        Button(
            onClick = {
                if (!isLocationGranted) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    viewModel.startJourney(startLocation, destination, selectedMode)
                }
            },
            enabled = startLocation.isNotBlank() && destination.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = KavachCyanPrimary,
                contentColor = KavachDarkBg,
                disabledContainerColor = KavachDarkSurfaceVariant,
                disabledContentColor = KavachTextMuted
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (!isLocationGranted) "GRANT PERMISSION & START" else "START JOURNEY",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(KavachDarkSurfaceVariant, RoundedCornerShape(8.dp))
            .border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = KavachTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TravelModeCard(
    mode: TravelMode,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val icon = when (mode) {
        TravelMode.CAB -> Icons.Default.LocalTaxi
        TravelMode.AUTO -> Icons.Default.ElectricRickshaw
        TravelMode.WALKING -> Icons.Default.DirectionsWalk
        TravelMode.TRANSIT -> Icons.Default.DirectionsBus
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) KavachCyanPrimary.copy(alpha = 0.15f) else KavachDarkSurface)
            .border(
                1.5.dp,
                if (isSelected) KavachCyanPrimary else KavachDarkCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = mode.label,
                tint = if (isSelected) KavachCyanPrimary else KavachTextSecondary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = mode.label,
                    color = if (isSelected) KavachTextPrimary else KavachTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = "~${mode.speedKmh} km/h",
                    color = KavachTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
