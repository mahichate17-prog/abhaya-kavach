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
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalTaxi
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
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachEmergencyBg
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachLavender
import com.example.ui.theme.KavachLavenderBg
import com.example.ui.theme.KavachPowderBlue
import com.example.ui.theme.KavachPowderBlueBg
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachSafeGreenBg
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
    var startLocation by remember {
        mutableStateOf("MG Road Metro Station, Gate 2")
    }

    var destination by remember {
        mutableStateOf("Whitefield Tech Park, Block B")
    }

    var selectedMode by remember {
        mutableStateOf(TravelMode.CAB)
    }

    val isLocationGranted by
        viewModel.isLocationPermissionGranted.collectAsState()

    val isLocationServiceEnabled by
        viewModel.isLocationServiceEnabled.collectAsState()

    val currentAddress by
        viewModel.currentAddress.collectAsState()

    val currentCoordinates by
        viewModel.currentCoordinates.collectAsState()

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            val coarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            val granted = fineGranted || coarseGranted

            viewModel.onPermissionResult(granted)

            if (granted) {
                viewModel.showToast(
                    "GPS Location Permission Granted"
                )
            } else {
                viewModel.showToast(
                    "Location permission is required for real-time tracking"
                )
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KavachDarkBg)
            .verticalScroll(scrollState)
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            )
    ) {

        // ─────────────────────────────
        // HEADER
        // ─────────────────────────────

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    viewModel.navigateTo(AppScreen.HOME)
                },
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        KavachLavenderBg,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = KavachLavender
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Plan your journey",
                    color = KavachTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "We'll keep an eye on the road",
                    color = KavachTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ─────────────────────────────
        // GPS STATUS
        // ─────────────────────────────

        Card(
            colors = CardDefaults.cardColors(
                containerColor =
                    if (
                        isLocationGranted &&
                        isLocationServiceEnabled
                    )
                        KavachSafeGreenBg
                    else
                        KavachEmergencyBg
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
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
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                )
                            )
                        } catch (_: Exception) {
                            viewModel.showToast(
                                "Please enable Location in device settings"
                            )
                        }
                    }
                }
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (
                                isLocationGranted &&
                                isLocationServiceEnabled
                            )
                                Color.White.copy(alpha = 0.75f)
                            else
                                Color.White.copy(alpha = 0.7f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector =
                            if (
                                isLocationGranted &&
                                isLocationServiceEnabled
                            )
                                Icons.Default.GpsFixed
                            else
                                Icons.Default.Warning,
                        contentDescription = null,
                        tint =
                            if (
                                isLocationGranted &&
                                isLocationServiceEnabled
                            )
                                KavachSafeGreen
                            else
                                KavachWarningAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Live location",
                        color = KavachTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = when {
                            !isLocationGranted ->
                                "Permission required"

                            !isLocationServiceEnabled ->
                                "Turn on device GPS"

                            else ->
                                "GPS connected • High accuracy"
                        },
                        color =
                            if (
                                isLocationGranted &&
                                isLocationServiceEnabled
                            )
                                KavachSafeGreen
                            else
                                KavachWarningAmber,
                        fontSize = 11.sp
                    )
                }

                Switch(
                    checked = isLocationGranted,
                    onCheckedChange = { checked ->

                        if (
                            checked &&
                            !isLocationGranted
                        ) {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        } else {
                            viewModel.setLocationPermission(
                                checked
                            )
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = KavachSafeGreen,
                        uncheckedThumbColor = KavachTextMuted,
                        uncheckedTrackColor = Color.White.copy(
                            alpha = 0.5f
                        )
                    )
                )
            }
        }

        if (
            !isLocationGranted ||
            !isLocationServiceEnabled
        ) {

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text =
                    if (!isLocationGranted)
                        "Location permission helps Abhaya Kavach monitor your journey in real time."
                    else
                        "Turn on device location services to enable live tracking.",
                color = KavachWarningAmber,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(
                    horizontal = 5.dp
                )
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // ─────────────────────────────
        // ROUTE CARD
        // ─────────────────────────────

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = "Where are you going?",
                    color = KavachTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Set your safe route below",
                    color = KavachTextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // START
                Text(
                    text = "STARTING POINT",
                    color = KavachTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = startLocation,
                    onValueChange = {
                        startLocation = it
                    },
                    placeholder = {
                        Text(
                            "Enter pickup location",
                            color = KavachTextMuted
                        )
                    },
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
                            fontSize = 10.sp,
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

                                        startLocation =
                                            if (
                                                currentAddress.isNotBlank() &&
                                                !currentAddress.contains(
                                                    "Acquiring"
                                                )
                                            ) {
                                                currentAddress
                                            } else {
                                                "Current GPS Location ($currentCoordinates)"
                                            }
                                    }
                                }
                                .background(
                                    KavachPowderBlueBg,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 5.dp
                                )
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KavachCyanPrimary,
                        unfocusedBorderColor = KavachDarkCardBorder,
                        focusedTextColor = KavachTextPrimary,
                        unfocusedTextColor = KavachTextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(7.dp)
                ) {

                    PresetChip(
                        label = "📍 Current GPS"
                    ) {

                        if (!isLocationGranted) {

                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )

                        } else {
                            startLocation =
                                "Current GPS Location ($currentCoordinates)"
                        }
                    }

                    PresetChip(
                        label = "🏢 Office"
                    ) {
                        startLocation =
                            "Embassy TechVillage Main Gate"
                    }

                    PresetChip(
                        label = "🚇 Metro"
                    ) {
                        startLocation =
                            "Indiranagar Metro Station"
                    }
                }

                Spacer(modifier = Modifier.height(17.dp))

                // DESTINATION
                Text(
                    text = "DESTINATION",
                    color = KavachTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = destination,
                    onValueChange = {
                        destination = it
                    },
                    placeholder = {
                        Text(
                            "Enter destination",
                            color = KavachTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = KavachEmergencyRed
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = KavachCyanPrimary,
                        unfocusedBorderColor = KavachDarkCardBorder,
                        focusedTextColor = KavachTextPrimary,
                        unfocusedTextColor = KavachTextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(7.dp)
                ) {

                    PresetChip(
                        label = "🏠 Home"
                    ) {
                        destination =
                            "Green Glen Layout, Flat 402"
                    }

                    PresetChip(
                        label = "🏢 Tech Park"
                    ) {
                        destination =
                            "Whitefield Tech Park, Block B"
                    }

                    PresetChip(
                        label = "🛍️ Mall"
                    ) {
                        destination =
                            "Phoenix Marketcity Main Entry"
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────
        // TRAVEL MODE
        // ─────────────────────────────

        Text(
            text = "How are you travelling?",
            color = KavachTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Choose your travel mode",
            color = KavachTextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            TravelModeCard(
                mode = TravelMode.CAB,
                isSelected =
                    selectedMode == TravelMode.CAB,
                modifier = Modifier.weight(1f)
            ) {
                selectedMode = TravelMode.CAB
            }

            TravelModeCard(
                mode = TravelMode.AUTO,
                isSelected =
                    selectedMode == TravelMode.AUTO,
                modifier = Modifier.weight(1f)
            ) {
                selectedMode = TravelMode.AUTO
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            TravelModeCard(
                mode = TravelMode.WALKING,
                isSelected =
                    selectedMode == TravelMode.WALKING,
                modifier = Modifier.weight(1f)
            ) {
                selectedMode = TravelMode.WALKING
            }

            TravelModeCard(
                mode = TravelMode.TRANSIT,
                isSelected =
                    selectedMode == TravelMode.TRANSIT,
                modifier = Modifier.weight(1f)
            ) {
                selectedMode = TravelMode.TRANSIT
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ─────────────────────────────
        // SAFE ROUTE SUMMARY
        // ─────────────────────────────

        Card(
            colors = CardDefaults.cardColors(
                containerColor = KavachPowderBlueBg
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = KavachPowderBlue,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Safe route ready",
                        color = KavachTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "8.4 km • ~22 mins",
                        color = KavachTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {

                    Text(
                        text = "98%",
                        color = KavachSafeGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "SAFETY SCORE",
                        color = KavachTextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ─────────────────────────────
        // START JOURNEY
        // ─────────────────────────────

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

                    viewModel.startJourney(
                        startLocation,
                        destination,
                        selectedMode
                    )
                }
            },
            enabled =
                startLocation.isNotBlank() &&
                destination.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = KavachCyanPrimary,
                contentColor = Color.White,
                disabledContainerColor =
                    KavachDarkCardBorder,
                disabledContentColor =
                    KavachTextMuted
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        ) {

            Icon(
                imageVector = Icons.Default.NearMe,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(9.dp))

            Text(
                text =
                    if (!isLocationGranted)
                        "GRANT LOCATION & START"
                    else
                        "START JOURNEY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your location is used to monitor your journey safely.",
            color = KavachTextMuted,
            fontSize = 9.sp,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
private fun PresetChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                Color.White,
                RoundedCornerShape(50.dp)
            )
            .border(
                1.dp,
                KavachDarkCardBorder,
                RoundedCornerShape(50.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 9.dp,
                vertical = 6.dp
            )
    ) {
        Text(
            text = label,
            color = KavachTextSecondary,
            fontSize = 10.sp,
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
        TravelMode.CAB ->
            Icons.Default.LocalTaxi

        TravelMode.AUTO ->
            Icons.Default.ElectricRickshaw

        TravelMode.WALKING ->
            Icons.Default.DirectionsWalk

        TravelMode.TRANSIT ->
            Icons.Default.DirectionsBus
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor =
                if (isSelected)
                    KavachLavenderBg
                else
                    Color.White
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                if (isSelected) 1.5.dp else 1.dp,
                if (isSelected)
                    KavachLavender
                else
                    KavachDarkCardBorder,
                RoundedCornerShape(18.dp)
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSelected)
                            Color.White.copy(alpha = 0.8f)
                        else
                            KavachPowderBlueBg,
                        CircleShape
                    )
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = mode.label,
                    tint =
                        if (isSelected)
                            KavachLavender
                        else
                            KavachCyanPrimary,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {

                Text(
                    text = mode.label,
                    color = KavachTextPrimary,
                    fontSize = 12.sp,
                    fontWeight =
                        if (isSelected)
                            FontWeight.Bold
                        else
                            FontWeight.Medium
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
