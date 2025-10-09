package no.uio.ifi.in2000.team47.rocketboy.model.grib

import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribData
import no.uio.ifi.in2000.team47.rocketboy.data.grib.WindShearData
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt


fun getGribData(gribResponse: GribResponse?, airPressure: Double): List<GribData> {
    val R = 287.05  // Spesific gassconstant for dry air (J/(kg·K))
    val g = 9.81    // Gravity (m/s^2)

    val isobarValues = gribResponse?.domain?.axes?.z?.values  // Isobaric values (hPa)
    val temperatureValues = gribResponse?.ranges?.temperature?.values  // Temperatures (°C)
    val windFromDirectionValues = gribResponse?.ranges?.wind_from_direction?.values  // Wind direction
    val windSpeedValues = gribResponse?.ranges?.wind_speed?.values  // Wind speed

    val gribData = mutableListOf<GribData>()

    if (isobarValues != null && temperatureValues != null && windFromDirectionValues != null && windSpeedValues != null) {
        for (i in isobarValues.indices) {
            val P = isobarValues[i]  // Pressure
            val T = temperatureValues.getOrNull(i) ?: continue  // Temperature
            val windFromDirection = windFromDirectionValues.getOrNull(i) ?: continue  // Wind direction
            val windSpeed = windSpeedValues.getOrNull(i) ?: continue  // Vindhastighet

            val T_kelvin = T + 273.15

            val deltaZ = (R * T_kelvin / g) * kotlin.math.ln(airPressure / P)

            val windDirection = (windFromDirection + 180) % 360

            gribData.add(
                GribData(
                    isobar = P.toInt(),
                    moh = deltaZ.roundToInt(),
                    temp = T,
                    windDirection = windDirection,
                    windSpeed = windSpeed
                )
            )
        }
    }

    return gribData
}

fun getShearWind(gribData: List<GribData>, lowerIsobar: Int, upperIsobar: Int): WindShearData? {
    val lowerLayer = gribData.find { it.isobar == lowerIsobar }
    val upperLayer = gribData.find { it.isobar == upperIsobar }

    if (lowerLayer == null || upperLayer == null) return null

    // Converting wind direction and speed to vector components
    fun windVector(windFromDirection: Double, windSpeed: Double): Pair<Double, Double> {
        val angleRad = Math.toRadians(windFromDirection) + Math.PI // Adjusting for metrological direction
        val u = -windSpeed * cos(angleRad) // U-component (east-west)
        val v = -windSpeed * sin(angleRad) // V-component (north-south)
        return Pair(u, v)
    }

    val (u1, v1) = windVector(lowerLayer.windDirection, lowerLayer.windSpeed)
    val (u2, v2) = windVector(upperLayer.windDirection, upperLayer.windSpeed)

    // Calculate shear wind as the length of the differential vector
    val shearMagnitude = sqrt((u2 - u1).pow(2) + (v2 - v1).pow(2)).let { "%.2f".format(it).toDouble() }
    val shearDirection = Math.toDegrees(atan2(v2 - v1, u2 - u1)).let { "%.2f".format(it).toDouble() }

    return WindShearData(
        lowerIsobar,
        upperIsobar,
        shearMagnitude,
        shearDirection,
        lowerLayer.windDirection,
        lowerLayer.windSpeed,
        upperLayer.windDirection,
        upperLayer.windSpeed
    )
}

fun convertToDisplayGribData(gribDataList: List<GribData>): List<WindShearData> {
    val result = mutableListOf<WindShearData>()
    for (i in 0 until gribDataList.size - 1) {
        val lower = gribDataList[i]
        val upper = gribDataList[i + 1]
        getShearWind(gribDataList, lower.isobar, upper.isobar)?.let { result.add(it) }
    }
    return result
}