package com.example.ui.components

import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.JourneySession
import com.example.model.PlannedRouteData
import com.example.ui.theme.KavachCyanPrimary
import com.example.ui.theme.KavachDarkBg
import com.example.ui.theme.KavachDarkCardBorder
import com.example.ui.theme.KavachDarkSurface
import com.example.ui.theme.KavachSafeGreen
import com.example.ui.theme.KavachTextPrimary
import com.example.ui.theme.KavachWarningAmber
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.Locale

@Composable
fun RealOsmMapVisualizer(
    journey: JourneySession?,
    plannedRoute: PlannedRouteData?,
    userLat: Double?,
    userLon: Double?,
    userSpeedKmh: Int,
    locationAccuracyMeters: Float?,
    isDeviating: Boolean,
    isRouteLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Initialize osmdroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var routePolylineOverlay by remember { mutableStateOf<Polyline?>(null) }
    var userMarkerOverlay by remember { mutableStateOf<Marker?>(null) }
    var startMarkerOverlay by remember { mutableStateOf<Marker?>(null) }
    var destMarkerOverlay by remember { mutableStateOf<Marker?>(null) }

    // Convert route polyline into osmdroid GeoPoints
    val routeGeoPoints = remember(plannedRoute) {
        plannedRoute?.polylineCoordinates?.map { GeoPoint(it.latitude, it.longitude) } ?: emptyList()
    }

    val defaultLat = userLat ?: plannedRoute?.origin?.latitude ?: 12.9716
    val defaultLon = userLon ?: plannedRoute?.origin?.longitude ?: 77.5946

    // Update Route Polyline & Markers on MapView
    LaunchedEffect(mapViewRef, plannedRoute, isDeviating) {
        val map = mapViewRef ?: return@LaunchedEffect

        // 1. Remove old overlays
        routePolylineOverlay?.let { map.overlays.remove(it) }
        startMarkerOverlay?.let { map.overlays.remove(it) }
        destMarkerOverlay?.let { map.overlays.remove(it) }

        // 2. Add Route Polyline
        if (routeGeoPoints.isNotEmpty()) {
            val polyline = Polyline(map).apply {
                setPoints(routeGeoPoints)
                val lineColor = if (isDeviating) KavachWarningAmber.toArgb() else KavachCyanPrimary.toArgb()
                outlinePaint.color = lineColor
                outlinePaint.strokeWidth = 14f
                outlinePaint.strokeCap = Paint.Cap.ROUND
                outlinePaint.strokeJoin = Paint.Join.ROUND
                outlinePaint.isAntiAlias = true
            }
            map.overlays.add(0, polyline) // Add at bottom of overlays
            routePolylineOverlay = polyline

            // 3. Add Origin Marker
            plannedRoute?.origin?.let { origin ->
                val originMarker = Marker(map).apply {
                    position = GeoPoint(origin.latitude, origin.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "START: ${journey?.startLocation ?: "Origin"}"
                    snippet = "Corridor Ingress Point"
                }
                map.overlays.add(originMarker)
                startMarkerOverlay = originMarker
            }

            // 4. Add Destination Marker
            plannedRoute?.destination?.let { dest ->
                val destMarker = Marker(map).apply {
                    position = GeoPoint(dest.latitude, dest.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "DESTINATION: ${journey?.destination ?: "Destination"}"
                    snippet = "Safe Corridor Egress Point"
                }
                map.overlays.add(destMarker)
                destMarkerOverlay = destMarker
            }

            // Fit bounds with padding
            try {
                if (routeGeoPoints.size >= 2) {
                    val boundingBox = BoundingBox.fromGeoPoints(routeGeoPoints)
                    map.zoomToBoundingBox(boundingBox, true, 80)
                }
            } catch (e: Exception) {
                map.controller.setCenter(routeGeoPoints.first())
                map.controller.setZoom(15.0)
            }
        }

        map.invalidate()
    }

    // Update User Marker Position
    LaunchedEffect(mapViewRef, userLat, userLon, userSpeedKmh, locationAccuracyMeters, isDeviating) {
        val map = mapViewRef ?: return@LaunchedEffect
        if (userLat != null && userLon != null) {
            val userGeoPoint = GeoPoint(userLat, userLon)

            if (userMarkerOverlay == null) {
                val marker = Marker(map).apply {
                    position = userGeoPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "LIVE GPS POSITION"
                    snippet = "$userSpeedKmh km/h • Accuracy: ${locationAccuracyMeters?.toInt() ?: 0}m"
                }
                map.overlays.add(marker)
                userMarkerOverlay = marker
            } else {
                userMarkerOverlay?.position = userGeoPoint
                userMarkerOverlay?.snippet = "$userSpeedKmh km/h • Accuracy: ${locationAccuracyMeters?.toInt() ?: 0}m"
            }

            map.invalidate()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(270.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(KavachDarkSurface)
            .border(1.dp, KavachDarkCardBorder, RoundedCornerShape(18.dp))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    isTilesScaledToDpi = true
                    zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(defaultLat, defaultLon))
                    mapViewRef = this
                }
            },
            update = { map ->
                mapViewRef = map
            }
        )

        // Overlay: Top Info Pill (Route distance & duration)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(KavachDarkBg.copy(alpha = 0.88f))
                .border(0.5.dp, KavachDarkCardBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Route,
                contentDescription = null,
                tint = KavachCyanPrimary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (plannedRoute != null) {
                    "${String.format(Locale.US, "%.1f km", plannedRoute.distanceKm)} • ${plannedRoute.durationMinutes} min (OSM)"
                } else {
                    "OPENSTREETMAP ROAD CORRIDOR"
                },
                color = KavachTextPrimary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        // Overlay: Quick Control Floating Action Buttons (Top Right)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Re-center on My Location
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(KavachDarkBg.copy(alpha = 0.9f))
                    .border(1.dp, KavachDarkCardBorder, CircleShape)
                    .clickable {
                        val map = mapViewRef
                        if (map != null && userLat != null && userLon != null) {
                            map.controller.animateTo(GeoPoint(userLat, userLon))
                            map.controller.setZoom(16.5)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = "My Location",
                    tint = KavachSafeGreen,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Fit Entire Route
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(KavachDarkBg.copy(alpha = 0.9f))
                    .border(1.dp, KavachDarkCardBorder, CircleShape)
                    .clickable {
                        val map = mapViewRef
                        if (map != null && routeGeoPoints.size >= 2) {
                            try {
                                val boundingBox = BoundingBox.fromGeoPoints(routeGeoPoints)
                                map.zoomToBoundingBox(boundingBox, true, 80)
                            } catch (e: Exception) {
                                map.controller.setCenter(routeGeoPoints.first())
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CropFree,
                    contentDescription = "Fit Route",
                    tint = KavachCyanPrimary,
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        // Loading overlay if route calculation is in progress
        AnimatedVisibility(
            visible = isRouteLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(KavachDarkBg.copy(alpha = 0.92f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = KavachCyanPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Calculating OSM Road Route...",
                        color = KavachTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
