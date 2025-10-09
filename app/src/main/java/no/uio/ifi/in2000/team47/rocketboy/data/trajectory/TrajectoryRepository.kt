package no.uio.ifi.in2000.team47.rocketboy.data.trajectory

import android.R.attr.x
import android.R.attr.y
import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribData
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Repository responsible for providing rocket trajectory data.
 * In a real application, this might fetch data from a network API,
 * a local database, or perform complex physics calculations.
 */
class TrajectoryRepository(
) {

    private val defaultLaunchPoint = Point.fromLngLat(10.7183, 59.9437)

    // Enkel fysikk-konstant: tyngdeakselerasjon
    private val g = 9.81

    fun generateParabolicTrajectory(
        startPoint: Point,
        initialSpeed: Double = 300.0, // m/s
        launchAngleDegrees: Double = 85.0,
        totalTime: Double = 30.0, // seconds
        timeStep: Double = 0.5,
        gravity: Double = 9.81,
        windEast: Double = 0.0, // m/s affecting longitude
        windNorth: Double = 0.0  // m/s affecting latitude
    ): List<Point> {
        val trajectory = mutableListOf<Point>()

        // Convert launch angle to radians
        val launchAngleRad = Math.toRadians(launchAngleDegrees)

        // Latitude/longitude degrees per meter (approx. at mid-latitudes)
        val metersPerDegreeLat = 111_000.0
        val metersPerDegreeLon = 111_000.0 * cos(Math.toRadians(startPoint.latitude()))

        var t = 0.0
        while (t <= totalTime) {
            // Horizontal (ground) distance
            val dx = initialSpeed * cos(launchAngleRad) * t + windEast * t
            val dy = initialSpeed * sin(launchAngleRad) * t - 0.5 * gravity * t * t + windNorth * t

            // Convert dx/dy from meters to lat/lon
            val deltaLon = dx / metersPerDegreeLon
            val deltaLat = dy / metersPerDegreeLat

            val newPoint = Point.fromLngLat(
                startPoint.longitude() + deltaLon,
                startPoint.latitude() + deltaLat
            )
            println("t=${t * timeStep}, x=$x, y=$y")

            trajectory.add(newPoint)
            t += timeStep
        }

        return trajectory
    }

    fun getLaunchTrajectory(
        startPoint: Point,
        gribDataList: List<GribData>
    ): List<Point> {
        // Her bruker vi f.eks. vind i de laveste 1000 meter
        val lowLevelWind = gribDataList
            .map { it.windSpeed to it.windDirection }
            .averageWindComponents()

        val (windEast, windNorth) = convertWindToVector(lowLevelWind.first, lowLevelWind.second)

        return generateParabolicTrajectory(
            startPoint = startPoint,
            initialSpeed = 300.0,
            launchAngleDegrees = 75.0,
            windEast = windEast,
            windNorth = windNorth
        )
    }

    fun convertWindToVector(speed: Double, directionDegrees: Double): Pair<Double, Double> {
        val angleRad = Math.toRadians((directionDegrees + 180) % 360)
        val windEast = speed * sin(angleRad)
        val windNorth = speed * cos(angleRad)
        return Pair(windEast, windNorth)
    }

    fun List<Pair<Double, Double>>.averageWindComponents(): Pair<Double, Double> {
        if (isEmpty()) return Pair(0.0, 0.0)
        val avgSpeed = this.map { it.first }.average()
        val avgDir = this.map { it.second }.average()
        return Pair(avgSpeed, avgDir)
    }

    fun getInitialLaunchPoint(): Point = defaultLaunchPoint

    /**
     * Returnerer vindhastighet og retning for en gitt høyde (moh).
     */
    private fun getWindAtAltitude(altitude: Double, gribDataList: List<GribData>): Pair<Double, Double> {
        val sorted = gribDataList.sortedBy { it.moh }
        val closest = sorted.minByOrNull { abs(it.moh - altitude) }
        return if (closest != null) {
            Pair(closest.windSpeed, closest.windDirection)
        } else {
            Pair(0.0, 0.0)
        }
    }
}
