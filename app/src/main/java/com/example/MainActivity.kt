package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppScreen
import com.example.ui.screens.EmergencyContactsScreen
import com.example.ui.screens.EmergencyModeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JourneyMonitoringScreen
import com.example.ui.screens.JourneySetupScreen
import com.example.ui.screens.SafetyCheckScreen
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachDarkSurfaceVariant
import com.example.ui.theme.KavachEmergencyRed
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachTextMuted
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachWarningAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SafetyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AbhayaKavachApp()
            }
        }
    }
}

@Composable
fun AbhayaKavachApp(viewModel: SafetyViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeJourney by viewModel.activeJourney.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Android Hardware / Gesture Back Handling
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        when (currentScreen) {
            AppScreen.JOURNEY_SETUP -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.EMERGENCY_CONTACTS -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.JOURNEY_MONITORING -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.SAFETY_CHECK -> {
                // Return to monitoring or prompt
                viewModel.confirmSafeResponse()
            }
            AppScreen.EMERGENCY_MODE -> {
                // Must explicitly cancel emergency
            }
            AppScreen.HOME -> { /* Exit */ }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = KavachDarkBg,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            // Android Bottom Navigation Bar (Hidden during full Emergency Mode for maximum clarity)
            if (currentScreen != AppScreen.EMERGENCY_MODE && currentScreen != AppScreen.SAFETY_CHECK) {
                NavigationBar(
                    containerColor = KavachDarkSurface,
                    contentColor = KavachTextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier.border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("HOME", fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KavachDarkBg,
                            selectedTextColor = KavachCyanPrimary,
                            indicatorColor = KavachCyanPrimary,
                            unselectedIconColor = KavachTextMuted,
                            unselectedTextColor = KavachTextMuted
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.JOURNEY_SETUP || currentScreen == AppScreen.JOURNEY_MONITORING,
                        onClick = {
                            if (activeJourney != null) {
                                viewModel.navigateTo(AppScreen.JOURNEY_MONITORING)
                            } else {
                                viewModel.navigateTo(AppScreen.JOURNEY_SETUP)
                            }
                        },
                        icon = { Icon(Icons.Default.Navigation, contentDescription = "Journey") },
                        label = { Text(if (activeJourney != null) "TRACK" else "JOURNEY", fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KavachDarkBg,
                            selectedTextColor = KavachCyanPrimary,
                            indicatorColor = KavachCyanPrimary,
                            unselectedIconColor = KavachTextMuted,
                            unselectedTextColor = KavachTextMuted
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.EMERGENCY_CONTACTS,
                        onClick = { viewModel.navigateTo(AppScreen.EMERGENCY_CONTACTS) },
                        icon = { Icon(Icons.Default.Contacts, contentDescription = "Contacts") },
                        label = { Text("GUARDIANS", fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KavachDarkBg,
                            selectedTextColor = KavachCyanPrimary,
                            indicatorColor = KavachCyanPrimary,
                            unselectedIconColor = KavachTextMuted,
                            unselectedTextColor = KavachTextMuted
                        )
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = { viewModel.activateEmergencyMode("Emergency Tab Bar Pressed") },
                        icon = { Icon(Icons.Default.PhoneInTalk, contentDescription = "SOS", tint = KavachEmergencyRed) },
                        label = { Text("SOS", fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = KavachEmergencyRed) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KavachEmergencyRed,
                            selectedTextColor = KavachEmergencyRed,
                            indicatorColor = KavachDarkSurfaceVariant,
                            unselectedIconColor = KavachEmergencyRed,
                            unselectedTextColor = KavachEmergencyRed
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            animationSpec = tween(280),
            modifier = Modifier.padding(innerPadding),
            label = "screen_crossfade"
        ) { screen ->
            when (screen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.JOURNEY_SETUP -> JourneySetupScreen(viewModel = viewModel)
                AppScreen.JOURNEY_MONITORING -> JourneyMonitoringScreen(viewModel = viewModel)
                AppScreen.SAFETY_CHECK -> SafetyCheckScreen(viewModel = viewModel)
                AppScreen.EMERGENCY_MODE -> EmergencyModeScreen(viewModel = viewModel)
                AppScreen.EMERGENCY_CONTACTS -> EmergencyContactsScreen(viewModel = viewModel)
            }
        }
    }
}
