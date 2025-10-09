package no.uio.ifi.in2000.team47.rocketboy.model.settings

import no.uio.ifi.in2000.team47.rocketboy.data.settings.CoordinatesData
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel

fun submitChanges(
    lat: Double,
    lon: Double,
    alt: Int,
    viewModel: WeatherViewModel,
    onLatError: (String?) -> Unit,
    onLonError: (String?) -> Unit,
    onAltError: (String?) -> Unit
): String? {
    val errorMessages = mutableListOf<String>()

    if (lat < 55.35 || lat > 64.25) {
        val msg = "Latitude must be between 55.35 and 64.25"
        onLatError(msg)
        errorMessages.add(msg)
    } else {
        onLatError(null)
    }

    if (lon < -1.45 || lon > 14.45) {
        val msg = "Longitude must be between -1.45 and 14.45"
        onLonError(msg)
        errorMessages.add(msg)
    } else {
        onLonError(null)
    }

    if (alt < 0) {
        val msg = "Altitude must be over 0"
        onAltError(msg)
        errorMessages.add(msg)
    } else {
        onAltError(null)
    }

    return if (errorMessages.isEmpty()) {
        viewModel.updateCoordinates(lat, lon, alt)
        null
    } else {
        errorMessages.first()
    }
}

fun parseCoordinatesInput(input: String): CoordinatesData {
    val trimmedInput = input.trim()

    // Støtt også format med komma mellom lat og lon (f.eks. "59.94, 10.73")
    val normalizedInput = trimmedInput.replace(",", " ")

    // DMS-format: "59°56'36.1"N 10°43'05.1"E"
    val dmsRegex = Regex("""(\d+)°(\d+)'(\d+(?:\.\d+)?)"([NS])\s+(\d+)°(\d+)'(\d+(?:\.\d+)?)"([EW])""")
    val dmsMatch = dmsRegex.find(normalizedInput)

    if (dmsMatch != null) {
        val (latDeg, latMin, latSec, latDir, lonDeg, lonMin, lonSec, lonDir) = dmsMatch.destructured
        val latitude = dmsToDecimal(latDeg.toInt(), latMin.toInt(), latSec.toDouble(), latDir)
        val longitude = dmsToDecimal(lonDeg.toInt(), lonMin.toInt(), lonSec.toDouble(), lonDir)
        return CoordinatesData(latitude, longitude, alt = 60)
    }

    // Desimalformat: "59.943233 10.736938" eller med høyde
    val parts = normalizedInput.split(Regex("""\s+"""))
    if (parts.size == 2 || parts.size == 3) {
        val lat = parts[0].toDoubleOrNull()
        val lon = parts[1].toDoubleOrNull()
        val alt = parts.getOrNull(2)?.toIntOrNull() ?: 60

        if (lat != null && lon != null) {
            return CoordinatesData(lat, lon, alt)
        }
    }
    throw IllegalArgumentException("Ugyldig koordinatformat: $input")
}




private fun dmsToDecimal(degrees: Int, minutes: Int, seconds: Double, direction: String): Double {
    var decimal = degrees + (minutes / 60.0) + (seconds / 3600.0)
    if (direction == "S" || direction == "W") {
        decimal *= -1
    }
    return decimal
}