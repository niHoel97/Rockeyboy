package no.uio.ifi.in2000.team47.rocketboy.data.grib

data class GribData(
    val isobar: Int,
    val moh: Int,
    val temp: Double,
    val windDirection: Double,
    val windSpeed: Double
)