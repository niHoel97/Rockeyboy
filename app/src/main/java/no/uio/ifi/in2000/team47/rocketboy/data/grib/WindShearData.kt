package no.uio.ifi.in2000.team47.rocketboy.data.grib

data class WindShearData(
    val lowerIsobar: Int,
    val upperIsobar: Int,
    val shearMagnitude: Double,
    val shearDirection: Double,
    val lowerWindDirection: Double,
    val lowerWindSpeed: Double,
    val upperWindDirection: Double,
    val upperWindSpeed: Double
)