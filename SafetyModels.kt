package com.example.model

import java.util.UUID

enum class AppScreen {
    HOME,
    JOURNEY_SETUP,
    JOURNEY_MONITORING,
    SAFETY_CHECK,
    EMERGENCY_MODE,
    EMERGENCY_CONTACTS
}

enum class TravelMode(val label: String, val iconName: String, val speedKmh: Int, val routingMode: String) {
    CAB("Cab / Taxi", "LocalTaxi", 35, "DRIVE"),
    AUTO("Auto Rickshaw", "ElectricRickshaw", 25, "TWO_WHEELER"),
    WALKING("Walking", "DirectionsWalk", 5, "WALK"),
    TRANSIT("Metro / Bus", "DirectionsBus", 30, "TRANSIT")
}

enum class SafetyStatus(val title: String, val description: String) {
    SAFE("JOURNEY SAFE", "Route is within verified safe corridor. Continuous monitoring active."),
    ROUTE_DEVIATION("UNUSUAL ROUTE DETECTED", "Vehicle has diverged from planned path by >300m."),
    UNEXPECTED_STOP("SUSPICIOUS HALT DETECTED", "Journey stationary in non-designated zone for >3 mins."),
    EMERGENCY("EMERGENCY ACTIVATED", "Alert dispatched to emergency contacts & response network.")
}

data class EmergencyContact(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val relationship: String,
    val isPrimary: Boolean = false
)

data class RoutePoint(
    val x: Float, // Normalized 0.0f to 1.0f on map canvas
    val y: Float,
    val label: String,
    val isDeviation: Boolean = false
)

data class GeoCoordinate(
    val latitude: Double,
    val longitude: Double,
    val name: String = ""
)

data class RouteWaypoint(
    val latitude: Double,
    val longitude: Double,
    val instruction: String? = null,
    val cumulativeDistanceMeters: Double = 0.0
)

data class PlannedRouteData(
    val origin: GeoCoordinate,
    val destination: GeoCoordinate,
    val polylineCoordinates: List<GeoCoordinate> = emptyList(),
    val encodedPolyline: String = "",
    val distanceMeters: Long = 0L,
    val durationSeconds: Long = 0L,
    val routePoints: List<RouteWaypoint> = emptyList(),
    val isRealRoadRoute: Boolean = true
) {
    val distanceKm: Double get() = if (distanceMeters > 0) distanceMeters / 1000.0 else 0.0
    val durationMinutes: Int get() = if (durationSeconds > 0) (durationSeconds / 60).toInt() else 0
}

data class JourneySession(
    val id: String = UUID.randomUUID().toString(),
    val startLocation: String,
    val destination: String,
    val mode: TravelMode,
    val distanceKm: Double,
    val estimatedMinutes: Int,
    val startTimeMillis: Long = System.currentTimeMillis(),
    val plannedRoute: PlannedRouteData? = null,
    val plannedPath: List<RoutePoint> = emptyList(),
    val deviationPath: List<RoutePoint> = emptyList()
)

data class AlertDispatchLog(
    val timestamp: String,
    val recipientName: String,
    val recipientPhone: String,
    val message: String,
    val status: String = "DELIVERED"
)
