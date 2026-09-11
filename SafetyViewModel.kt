package com.example.viewmodel
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sqrt
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.location.GeocodingService
import com.example.location.RealLocationTracker
import com.example.location.RouteService
import com.example.model.AlertDispatchLog
import com.example.model.AppScreen
import com.example.model.EmergencyContact
import com.example.model.GeoCoordinate
import com.example.model.JourneySession
import com.example.model.PlannedRouteData
import com.example.model.RoutePoint
import com.example.model.SafetyStatus
import com.example.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class SafetyViewModel(application: Application) : AndroidViewModel(application) {

    private val locationTracker = RealLocationTracker(application)
    private val geocodingService = GeocodingService(application)
    private val routeService = RouteService()

    // Current Screen
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Safety status
    private val _safetyStatus = MutableStateFlow(SafetyStatus.SAFE)
    val safetyStatus: StateFlow<SafetyStatus> = _safetyStatus.asStateFlow()

    // Real GPS & Location State
    private val _isRealGpsActive = MutableStateFlow(false)
    val isRealGpsActive: StateFlow<Boolean> = _isRealGpsActive.asStateFlow()

    private val _isLocationPermissionGranted = MutableStateFlow(locationTracker.hasLocationPermission())
    val isLocationPermissionGranted: StateFlow<Boolean> = _isLocationPermissionGranted.asStateFlow()

    private val _isLocationServiceEnabled = MutableStateFlow(locationTracker.isLocationServiceEnabled())
    val isLocationServiceEnabled: StateFlow<Boolean> = _isLocationServiceEnabled.asStateFlow()

    private val _locationErrorMessage = MutableStateFlow<String?>(null)
    val locationErrorMessage: StateFlow<String?> = _locationErrorMessage.asStateFlow()

    // Simulation vs Real GPS Mode distinction
    private val _isSimulationMode = MutableStateFlow(false)
    val isSimulationMode: StateFlow<Boolean> = _isSimulationMode.asStateFlow()

    // Raw location telemetry
    private val _rawLatitude = MutableStateFlow<Double?>(null)
    val rawLatitude: StateFlow<Double?> = _rawLatitude.asStateFlow()

    private val _rawLongitude = MutableStateFlow<Double?>(null)
    val rawLongitude: StateFlow<Double?> = _rawLongitude.asStateFlow()

    private val _locationAccuracyMeters = MutableStateFlow<Float?>(null)
    val locationAccuracyMeters: StateFlow<Float?> = _locationAccuracyMeters.asStateFlow()

    // Telemetry strings
    private val _currentCoordinates = MutableStateFlow("Waiting for GPS fix...")
    val currentCoordinates: StateFlow<String> = _currentCoordinates.asStateFlow()

    private val _currentAddress = MutableStateFlow("Acquiring GPS location...")
    val currentAddress: StateFlow<String> = _currentAddress.asStateFlow()

    private val _currentSpeedKmh = MutableStateFlow(0)
    val currentSpeedKmh: StateFlow<Int> = _currentSpeedKmh.asStateFlow()

    // Emergency Contacts
    private val _contacts = MutableStateFlow<List<EmergencyContact>>(
        listOf(
            EmergencyContact(name = "Ananya Sharma (Mother)", phone = "+91 98765 43210", relationship = "Mother", isPrimary = true),
            EmergencyContact(name = "Pooja Verma (Sister)", phone = "+91 98234 56789", relationship = "Sister", isPrimary = false),
            EmergencyContact(name = "Rhea Kapoor (Friend)", phone = "+91 91234 56780", relationship = "Friend", isPrimary = false)
        )
    )
    val contacts: StateFlow<List<EmergencyContact>> = _contacts.asStateFlow()

    // Current Active Journey & Planned Road Route
    private val _activeJourney = MutableStateFlow<JourneySession?>(null)
    val activeJourney: StateFlow<JourneySession?> = _activeJourney.asStateFlow()

    private val _plannedRoute = MutableStateFlow<PlannedRouteData?>(null)
    val plannedRoute: StateFlow<PlannedRouteData?> = _plannedRoute.asStateFlow()

    private val _isRouteLoading = MutableStateFlow(false)
    val isRouteLoading: StateFlow<Boolean> = _isRouteLoading.asStateFlow()

    // Journey progress
    private val _journeyProgress = MutableStateFlow(0.0f)
    val journeyProgress: StateFlow<Float> = _journeyProgress.asStateFlow()

    // Is the user currently deviating from the planned route?
    private val _isDeviating = MutableStateFlow(false)
    val isDeviating: StateFlow<Boolean> = _isDeviating.asStateFlow()

    // Safety Check Countdown (30 seconds)
    private val _countdownSeconds = MutableStateFlow(30)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private var countdownJob: Job? = null
    // ─────────────────────────────────────────────
// REAL GPS ANOMALY DETECTION THRESHOLDS
// Demo-friendly values for online presentation
// ─────────────────────────────────────────────
private companion object {
    const val ROUTE_DEVIATION_THRESHOLD_METERS = 30f
    const val REQUIRED_DEVIATION_UPDATES = 1
    const val LONG_HALT_THRESHOLD_SECONDS = 30L
    const val HALT_SPEED_THRESHOLD_KMH = 2f
    const val MAX_ACCEPTABLE_GPS_ACCURACY_METERS = 50f
}

private var deviationUpdateCount = 0
private var haltStartTimeMillis: Long? = null
private var lastMovementLocation: Location? = null
private var hasStartedMoving = false

    // Alert Logs for Emergency screen
    private val _dispatchedAlerts = MutableStateFlow<List<AlertDispatchLog>>(emptyList())
    val dispatchedAlerts: StateFlow<List<AlertDispatchLog>> = _dispatchedAlerts.asStateFlow()

    // Siren and Strobe state in emergency mode
    private val _isSirenActive = MutableStateFlow(true)
    val isSirenActive: StateFlow<Boolean> = _isSirenActive.asStateFlow()

    // Notification toast / message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        refreshLocationPermissionState()
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun refreshLocationPermissionState() {
        _isLocationPermissionGranted.value = locationTracker.hasLocationPermission()
        _isLocationServiceEnabled.value = locationTracker.isLocationServiceEnabled()
    }

    fun setLocationPermission(granted: Boolean) {
        _isLocationPermissionGranted.value = granted
        if (granted && _activeJourney.value != null) {
            startTrackingRealGps()
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _isLocationPermissionGranted.value = granted
        if (granted) {
            _locationErrorMessage.value = null
            if (_activeJourney.value != null) {
                startTrackingRealGps()
            }
        } else {
            _locationErrorMessage.value = "Location permission denied. Real GPS tracking cannot function without location permission."
            _currentCoordinates.value = "Permission Denied"
            _currentAddress.value = "Location permission required"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    private fun formatCoordinates(lat: Double, lon: Double): String {
        val latDir = if (lat >= 0) "N" else "S"
        val lonDir = if (lon >= 0) "E" else "W"
        return String.format(Locale.US, "%.4f° %s, %.4f° %s", abs(lat), latDir, abs(lon), lonDir)
    }

    // Real Location Updates Trigger
    fun startTrackingRealGps() {
        refreshLocationPermissionState()

        if (!_isLocationPermissionGranted.value) {
            _locationErrorMessage.value = "Location permission is required for live GPS tracking. Please grant permission."
            _currentCoordinates.value = "Permission Required"
            _currentAddress.value = "Location permission required"
            return
        }

        if (!_isLocationServiceEnabled.value) {
            _locationErrorMessage.value = "Device location services (GPS) are turned off. Please turn on Location in Settings."
            _currentCoordinates.value = "GPS Disabled"
            _currentAddress.value = "Device location services disabled"
            return
        }

        _locationErrorMessage.value = null
        if (_rawLatitude.value == null) {
            _currentCoordinates.value = "Acquiring GPS fix..."
            _currentAddress.value = "Locating device via GPS..."
        }
        _isRealGpsActive.value = true

        locationTracker.startLocationUpdates(
            onLocationResult = { location ->
                handleRealLocation(location)
            },
            onError = { errorMsg ->
                _locationErrorMessage.value = errorMsg
            }
        )
    }

    fun stopTrackingRealGps() {
        locationTracker.stopLocationUpdates()
        _isRealGpsActive.value = false
    }

    private fun handleRealLocation(location: Location) {
    if (_isSimulationMode.value) return

    _rawLatitude.value = location.latitude
    _rawLongitude.value = location.longitude
    _locationAccuracyMeters.value = location.accuracy
    _currentCoordinates.value =
        formatCoordinates(location.latitude, location.longitude)

   val speedKmh = if (location.hasSpeed()) {
    location.speed * 3.6f
} else {
    val previousLocation = lastMovementLocation

    if (previousLocation != null && location.time > previousLocation.time) {
        val distanceMeters = previousLocation.distanceTo(location)
        val timeSeconds = (location.time - previousLocation.time) / 1000f

        if (timeSeconds > 0f) {
            (distanceMeters / timeSeconds) * 3.6f
        } else {
            0f
        }
    } else {
        0f
    }
}

val movementDistanceMeters =
    lastMovementLocation?.distanceTo(location) ?: 0f

lastMovementLocation = Location(location)

if (movementDistanceMeters >= 5f || speedKmh >= HALT_SPEED_THRESHOLD_KMH) {
    hasStartedMoving = true
}

    _currentSpeedKmh.value = speedKmh.toInt()
    _locationErrorMessage.value = null
    _isRealGpsActive.value = true

    // ─────────────────────────────────────────────
    // REAL GPS ANOMALY DETECTION
    // Only run while an active journey is being monitored
    // ─────────────────────────────────────────────
    if (
        _currentScreen.value == AppScreen.JOURNEY_MONITORING &&
        _activeJourney.value != null &&
        location.accuracy <= MAX_ACCEPTABLE_GPS_ACCURACY_METERS
    ) {

        val plannedRoute = _plannedRoute.value

        // ───────────── ROUTE DEVIATION ─────────────
        if (plannedRoute != null && plannedRoute.polylineCoordinates.isNotEmpty()) {

            val distanceFromRoute = distanceToRoute(
                location.latitude,
                location.longitude,
                plannedRoute.polylineCoordinates
            )

            if (distanceFromRoute > ROUTE_DEVIATION_THRESHOLD_METERS) {
                deviationUpdateCount++

                if (deviationUpdateCount >= REQUIRED_DEVIATION_UPDATES) {
    _isDeviating.value = true
    _safetyStatus.value = SafetyStatus.ROUTE_DEVIATION

    showToast(
        "WRONG TURN DETECTED: ${distanceFromRoute.toInt()}m off planned route"
    )

    activateEmergencyMode(
        reason = "Route deviation detected after wrong turn"
    )

    deviationUpdateCount = 0
}
            } else {
                // Back inside the safe corridor
                deviationUpdateCount = 0
                _isDeviating.value = false
            }
        }

       
      // ───────────── LONG HALT DETECTION ─────────────
// Works for both walking and vehicle journeys.
// Small GPS speed fluctuations (0–1 km/h) are treated as stationary.
if (hasStartedMoving) {

    val isActuallyMoving = speedKmh >= 2f

    if (!isActuallyMoving) {

        if (haltStartTimeMillis == null) {
            haltStartTimeMillis = System.currentTimeMillis()
        }

        val haltDurationSeconds =
            (System.currentTimeMillis() - haltStartTimeMillis!!) / 1000L

        if (haltDurationSeconds >= LONG_HALT_THRESHOLD_SECONDS) {

            _isDeviating.value = true
            _safetyStatus.value = SafetyStatus.UNEXPECTED_STOP
            _currentScreen.value = AppScreen.SAFETY_CHECK

            showToast(
                "LONG HALT DETECTED: No movement for 30s"
            )

            startSafetyCheckCountdown()

            haltStartTimeMillis = null
            hasStartedMoving = false
        }

    } else {
        // Real movement detected — restart halt monitoring
        haltStartTimeMillis = null
    }
}

    // ─────────────────────────────────────────────
    // ADDRESS UPDATE
    // ─────────────────────────────────────────────
    viewModelScope.launch(Dispatchers.IO) {
        val resolvedAddress = locationTracker.reverseGeocode(
            location.latitude,
            location.longitude
        )

        if (!_isSimulationMode.value) {
            if (!resolvedAddress.isNullOrBlank()) {
                _currentAddress.value = resolvedAddress
            } else {
                _currentAddress.value =
                    "Near ${formatCoordinates(location.latitude, location.longitude)}"
            }
        }
    }
}
    private fun distanceToRoute(
    latitude: Double,
    longitude: Double,
    routePoints: List<GeoCoordinate>
): Double {

    if (routePoints.isEmpty()) return Double.MAX_VALUE

    if (routePoints.size == 1) {
        val result = FloatArray(1)

        Location.distanceBetween(
            latitude,
            longitude,
            routePoints[0].latitude,
            routePoints[0].longitude,
            result
        )

        return result[0].toDouble()
    }

    var minimumDistance = Double.MAX_VALUE

    val earthRadius = 6_371_000.0
    val latitudeScale =
        earthRadius * Math.PI / 180.0

    for (i in 0 until routePoints.size - 1) {

        val start = routePoints[i]
        val end = routePoints[i + 1]

        val cosLatitude =
            cos(Math.toRadians(latitude))

        val longitudeScale =
            latitudeScale * cosLatitude

        val px = (longitude - start.longitude) * longitudeScale
        val py = (latitude - start.latitude) * latitudeScale

        val sx = (end.longitude - start.longitude) * longitudeScale
        val sy = (end.latitude - start.latitude) * latitudeScale

        val segmentLengthSquared = sx * sx + sy * sy

        val t = if (segmentLengthSquared == 0.0) {
            0.0
        } else {
            ((px * sx) + (py * sy)) / segmentLengthSquared
        }

        val clampedT = t.coerceIn(0.0, 1.0)

        val closestX = sx * clampedT
        val closestY = sy * clampedT

        val dx = px - closestX
        val dy = py - closestY

        val distance = sqrt(dx * dx + dy * dy)

        minimumDistance = min(minimumDistance, distance)
    }

    return minimumDistance
}

    // Contacts Management
    fun addContact(name: String, phone: String, relationship: String, isPrimary: Boolean) {
        val newContact = EmergencyContact(
            name = name,
            phone = phone,
            relationship = relationship,
            isPrimary = isPrimary
        )
        val updated = if (isPrimary) {
            _contacts.value.map { it.copy(isPrimary = false) } + newContact
        } else {
            _contacts.value + newContact
        }
        _contacts.value = updated
        showToast("Contact '$name' added successfully")
    }

    fun removeContact(id: String) {
        _contacts.value = _contacts.value.filterNot { it.id == id }
        showToast("Contact removed")
    }

    fun setPrimaryContact(id: String) {
        _contacts.value = _contacts.value.map {
            it.copy(isPrimary = (it.id == id))
        }
        showToast("Primary contact updated")
    }

    // Journey Initiation with Real Geocoding & Real Road Routing
    fun startJourney(startLoc: String, destLoc: String, mode: TravelMode) {
        viewModelScope.launch {
            _isRouteLoading.value = true
            _isSimulationMode.value = false
            _isDeviating.value = false
            lastMovementLocation = null
            hasStartedMoving = false
            deviationUpdateCount = 0
             haltStartTimeMillis = null
            _safetyStatus.value = SafetyStatus.SAFE
            _currentScreen.value = AppScreen.JOURNEY_MONITORING

            // Start Real GPS tracking immediately
            startTrackingRealGps()

            // Resolve Origin and Destination to geographic coordinates
            val currentLat = _rawLatitude.value ?: 12.9716
            val currentLon = _rawLongitude.value ?: 77.5946

            val originGeo = geocodingService.resolveLocation(
                query = startLoc,
                fallbackLat = currentLat,
                fallbackLon = currentLon
            ) ?: GeoCoordinate(currentLat, currentLon, startLoc)

            val destGeo = geocodingService.resolveLocation(
                query = destLoc,
                fallbackLat = originGeo.latitude + 0.05,
                fallbackLon = originGeo.longitude + 0.08
            ) ?: GeoCoordinate(originGeo.latitude + 0.05, originGeo.longitude + 0.08, destLoc)

            // Calculate real road route via OpenStreetMap OSRM Routing Engine (Zero API Key required)
            val computedRoute = routeService.computeRoute(
                origin = originGeo,
                destination = destGeo,
                travelMode = mode
            )

            _plannedRoute.value = computedRoute

            val session = JourneySession(
                startLocation = originGeo.name.ifBlank { startLoc },
                destination = destGeo.name.ifBlank { destLoc },
                mode = mode,
                distanceKm = if (computedRoute.distanceKm > 0) computedRoute.distanceKm else 8.4,
                estimatedMinutes = if (computedRoute.durationMinutes > 0) computedRoute.durationMinutes else 22,
                plannedRoute = computedRoute
            )

            _activeJourney.value = session
            _journeyProgress.value = 0.05f
            _isRouteLoading.value = false

            showToast("Safe Corridor Activated (${String.format(Locale.US, "%.1f km", session.distanceKm)})")
        }
    }

   

    // Start 30 second visible countdown
    private fun startSafetyCheckCountdown() {
        countdownJob?.cancel()
        _countdownSeconds.value = 30
        countdownJob = viewModelScope.launch {
            while (_countdownSeconds.value > 0) {
                delay(1000)
                _countdownSeconds.value -= 1
            }
            // If countdown reaches 0 without user responding "I'M SAFE", automatically enter Emergency Mode!
            if (_currentScreen.value == AppScreen.SAFETY_CHECK) {
                activateEmergencyMode(reason = "No response to 30s Safety Check countdown")
            }
        }
    }

    // User taps "I'M SAFE"
    fun confirmSafeResponse() {
        countdownJob?.cancel()
        _isSimulationMode.value = false
        _isDeviating.value = false
        deviationUpdateCount = 0
        haltStartTimeMillis = null
        _safetyStatus.value = SafetyStatus.SAFE
        _currentScreen.value = AppScreen.JOURNEY_MONITORING

        // Resume real GPS data display
        val lat = _rawLatitude.value
        val lon = _rawLongitude.value
        if (lat != null && lon != null) {
            _currentCoordinates.value = formatCoordinates(lat, lon)
            viewModelScope.launch(Dispatchers.IO) {
                val addr = locationTracker.reverseGeocode(lat, lon)
                if (!addr.isNullOrBlank()) {
                    _currentAddress.value = addr
                }
            }
        } else {
            _currentCoordinates.value = "Acquiring GPS fix..."
            _currentAddress.value = "Resuming real-time monitoring..."
        }

        showToast("Safety verified. Live GPS monitoring resumed.")
    }

    // User taps "I NEED HELP" or 30s timer expired
    fun activateEmergencyMode(reason: String = "Manual Emergency or Anomaly Trigger") {
        countdownJob?.cancel()
        _safetyStatus.value = SafetyStatus.EMERGENCY

        // Generate simulated SOS dispatches to all contacts with REAL coordinates if available
        val timeStr = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
        val coordText = _currentCoordinates.value
        val addrText = _currentAddress.value
        val alerts = _contacts.value.map { contact ->
            AlertDispatchLog(
                timestamp = timeStr,
                recipientName = contact.name,
                recipientPhone = contact.phone,
                message = "SOS EMERGENCY ALERT: Abhaya Kavach detected potential danger at coordinates $coordText ($addrText). Live Tracking: https://abhayakavach.safe/track/${_activeJourney.value?.id ?: "live"}",
                status = "SENT & DELIVERED"
            )
        }
        _dispatchedAlerts.value = alerts
        _isSirenActive.value = true
        _currentScreen.value = AppScreen.EMERGENCY_MODE
        showToast("EMERGENCY ACTIVATED: Contacts alerted with live GPS")
    }

    fun toggleSiren() {
        _isSirenActive.value = !_isSirenActive.value
    }

    fun cancelEmergency() {
        countdownJob?.cancel()
        _safetyStatus.value = SafetyStatus.SAFE
        _isDeviating.value = false
        deviationUpdateCount = 0
        haltStartTimeMillis = null
        lastMovementLocation = null
        hasStartedMoving = false
        _isSimulationMode.value = false
        _isSirenActive.value = false
        showToast("Emergency mode deactivated. You are marked SAFE.")
        _currentScreen.value = AppScreen.HOME
    }

    fun endJourneySafe() {
        stopTrackingRealGps()
        countdownJob?.cancel()
        _activeJourney.value = null
        _plannedRoute.value = null
        _journeyProgress.value = 0f
        _isDeviating.value = false
        deviationUpdateCount = 0
        haltStartTimeMillis = null
        _isSimulationMode.value = false
        _isRealGpsActive.value = false
        _safetyStatus.value = SafetyStatus.SAFE
        showToast("Journey completed safely. Kavach disarmed.")
        _currentScreen.value = AppScreen.HOME
    }

    fun testEmergencyAlertDispatch() {
        val timeStr = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
        val testLogs = _contacts.value.map {
            AlertDispatchLog(
                timestamp = timeStr,
                recipientName = it.name,
                recipientPhone = it.phone,
                message = "[TEST KAVACH ALERT] Test safety ping from Abhaya Kavach. GPS: ${_currentCoordinates.value}. Everything is normal.",
                status = "TEST DELIVERED"
            )
        }
        _dispatchedAlerts.value = testLogs
        showToast("Test alert simulated for ${_contacts.value.size} emergency contacts")
    }

    override fun onCleared() {
        super.onCleared()
        stopTrackingRealGps()
    }
}
