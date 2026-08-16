package com.example.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.model.GeoCoordinate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * OpenStreetMap-based Geocoding service using OpenStreetMap Nominatim API,
 * with Android system Geocoder and local landmark fallbacks.
 * Completely FREE and requires NO API KEY.
 */
class GeocodingService(private val context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    /**
     * Resolves an address or place name into geographic coordinates.
     * 1. Check explicit lat,lon strings
     * 2. Query OpenStreetMap Nominatim Geocoding API
     * 3. Fallback to Android system Geocoder
     * 4. Fallback to known landmark database or offset coordinate
     */
    suspend fun resolveLocation(
        query: String,
        fallbackLat: Double? = null,
        fallbackLon: Double? = null
    ): GeoCoordinate? = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@withContext null

        // 1. Check if user entered explicit coordinates e.g. "12.9716, 77.5946"
        val parsedCoords = parseCoordinates(trimmed)
        if (parsedCoords != null) {
            return@withContext parsedCoords
        }

        // 2. Check if this is "Current GPS Location" and we have fallback coordinates
        if ((trimmed.startsWith("Current GPS", ignoreCase = true) || trimmed.equals("Current Location", ignoreCase = true))
            && fallbackLat != null && fallbackLon != null
        ) {
            val addr = reverseGeocode(fallbackLat, fallbackLon) ?: trimmed
            return@withContext GeoCoordinate(
                latitude = fallbackLat,
                longitude = fallbackLon,
                name = addr
            )
        }

        // 3. Query OpenStreetMap Nominatim Search API
        val nominatimResult = queryOsmNominatim(trimmed)
        if (nominatimResult != null) {
            return@withContext nominatimResult
        }

        // 4. Android Geocoder fallback
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(trimmed, 3)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val label = formatAddressLabel(address, trimmed)
                    return@withContext GeoCoordinate(
                        latitude = address.latitude,
                        longitude = address.longitude,
                        name = label
                    )
                }
            }
        } catch (e: Exception) {
            // Android geocoder network timeout or unavail
        }

        // 5. Known landmark database fallback
        val knownCoords = getFallbackLandmarkCoords(trimmed, fallbackLat, fallbackLon)
        if (knownCoords != null) {
            return@withContext knownCoords
        }

        // 6. Last resort
        if (fallbackLat != null && fallbackLon != null) {
            return@withContext GeoCoordinate(
                latitude = fallbackLat,
                longitude = fallbackLon,
                name = trimmed
            )
        }

        null
    }

    /**
     * Query OpenStreetMap Nominatim API for forward geocoding.
     */
    private fun queryOsmNominatim(query: String): GeoCoordinate? {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=1&addressdetails=1"

            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "AbhayaKavach-SafetyApp/1.0 (Android; OpenStreetMap)")
                .addHeader("Accept", "application/json")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return null
                    val jsonArray = JSONArray(body)
                    if (jsonArray.length() > 0) {
                        val first = jsonArray.getJSONObject(0)
                        val lat = first.optDouble("lat", Double.NaN)
                        val lon = first.optDouble("lon", Double.NaN)
                        val displayName = first.optString("display_name", query)
                        if (!lat.isNaN() && !lon.isNaN()) {
                            // Extract concise display name (e.g. first 2 parts)
                            val shortName = displayName.split(",").take(3).joinToString(",").trim()
                            return GeoCoordinate(
                                latitude = lat,
                                longitude = lon,
                                name = if (shortName.isNotBlank()) shortName else query
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Continue to next fallback
        }
        return null
    }

    /**
     * Reverse geocodes coordinates to street address using OpenStreetMap Nominatim.
     */
    suspend fun reverseGeocode(latitude: Double, longitude: Double): String? = withContext(Dispatchers.IO) {
        // 1. Try Nominatim Reverse Geocoding
        try {
            val url = "https://nominatim.openstreetmap.org/reverse?lat=$latitude&lon=$longitude&format=json&zoom=18&addressdetails=1"
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "AbhayaKavach-SafetyApp/1.0 (Android; OpenStreetMap)")
                .addHeader("Accept", "application/json")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@use
                    val json = JSONObject(body)
                    val displayName = json.optString("display_name")
                    if (displayName.isNotBlank()) {
                        val shortName = displayName.split(",").take(3).joinToString(",").trim()
                        return@withContext shortName
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback to Android Geocoder
        }

        // 2. Android Geocoder fallback
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    return@withContext formatAddressLabel(address, "")
                }
            }
        } catch (e: Exception) {
            // Null return
        }

        null
    }

    private fun formatAddressLabel(address: Address, fallback: String): String {
        val feature = address.featureName
        val thoroughfare = address.thoroughfare
        val subLocality = address.subLocality
        val locality = address.locality
        val parts = listOfNotNull(
            thoroughfare ?: feature,
            subLocality ?: locality
        ).filter { it.isNotBlank() }.distinct()

        return if (parts.isNotEmpty()) {
            parts.joinToString(", ")
        } else {
            address.getAddressLine(0) ?: fallback
        }
    }

    private fun parseCoordinates(text: String): GeoCoordinate? {
        val regex = Pattern.compile("([-+]?\\d{1,2}(?:\\.\\d+)?)[^\\d-+]+([-+]?\\d{1,3}(?:\\.\\d+)?)")
        val matcher = regex.matcher(text)
        if (matcher.find()) {
            val lat = matcher.group(1)?.toDoubleOrNull()
            val lon = matcher.group(2)?.toDoubleOrNull()
            if (lat != null && lon != null && lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180) {
                return GeoCoordinate(lat, lon, text)
            }
        }
        return null
    }

    private fun getFallbackLandmarkCoords(name: String, baseLat: Double?, baseLon: Double?): GeoCoordinate? {
        val lower = name.lowercase()
        return when {
            lower.contains("mg road") || lower.contains("metro") -> GeoCoordinate(12.9756, 77.6066, name)
            lower.contains("whitefield") || lower.contains("tech park") -> GeoCoordinate(12.9698, 77.7499, name)
            lower.contains("indiranagar") -> GeoCoordinate(12.9719, 77.6412, name)
            lower.contains("green glen") || lower.contains("bellandur") -> GeoCoordinate(12.9260, 77.6762, name)
            lower.contains("embassy tech") -> GeoCoordinate(12.9352, 77.6946, name)
            lower.contains("phoenix") || lower.contains("mall") -> GeoCoordinate(12.9959, 77.6964, name)
            lower.contains("airport") -> GeoCoordinate(13.1986, 77.7066, name)
            baseLat != null && baseLon != null -> {
                val offsetLat = (name.hashCode() % 100) * 0.0003
                val offsetLon = ((name.hashCode() / 100) % 100) * 0.0003
                GeoCoordinate(baseLat + offsetLat, baseLon + offsetLon, name)
            }
            else -> GeoCoordinate(12.9716, 77.5946, name)
        }
    }
}
