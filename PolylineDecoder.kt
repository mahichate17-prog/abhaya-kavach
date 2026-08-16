package com.example.location

import com.example.model.GeoCoordinate

object PolylineDecoder {

    /**
     * Decodes a standard Google Maps encoded polyline string (5-digit precision)
     * into a list of [GeoCoordinate].
     */
    fun decode(encoded: String): List<GeoCoordinate> {
        val poly = ArrayList<GeoCoordinate>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        try {
            while (index < len) {
                var b: Int
                var shift = 0
                var result = 0
                do {
                    b = encoded[index++].code - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20 && index < len)
                val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dlat

                shift = 0
                result = 0
                do {
                    if (index >= len) break
                    b = encoded[index++].code - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20 && index < len)
                val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dlng

                val pLat = lat.toDouble() / 1E5
                val pLng = lng.toDouble() / 1E5
                poly.add(GeoCoordinate(latitude = pLat, longitude = pLng))
            }
        } catch (e: Exception) {
            // If malformed or partial string, return whatever was successfully decoded
        }
        return poly
    }
}
