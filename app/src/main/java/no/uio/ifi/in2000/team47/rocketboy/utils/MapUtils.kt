package no.uio.ifi.in2000.team47.rocketboy.utils

import com.mapbox.geojson.Point
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MapUtils {
    /**
     * Calculates the bearing between two points.
     * Bearing is the angle in degrees clockwise from North.
     */
    fun calculateBearing(from: Point, to: Point): Double {
        val lon1 = Math.toRadians(from.longitude())
        val lat1 = Math.toRadians(from.latitude())
        val lon2 = Math.toRadians(to.longitude())
        val lat2 = Math.toRadians(to.latitude())

        val deltaLon = lon2 - lon1
        val y = sin(deltaLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLon)
        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360) % 360 // Normalize to 0-360
        return bearing
    }

    fun calculateDistance(from: Point, to: Point): Double {
        val R = 6371000.0
        val lat1 = Math.toRadians(from.latitude())
        val lat2 = Math.toRadians(to.latitude())
        val deltaLat = Math.toRadians(to.latitude() - from.latitude())
        val deltaLon = Math.toRadians(to.longitude() - from.longitude())
        val a = sin(deltaLat / 2) * sin(deltaLat / 2) + cos(lat1) * cos(lat2) * sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
