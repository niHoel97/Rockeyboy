package no.uio.ifi.in2000.team47.rocketboy.data.grib

import no.uio.ifi.in2000.team47.rocketboy.model.grib.GribResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class GribRepository(private val dataSource: GribDataSource) {
    suspend fun fetchGrib(lat: Double, lon: Double): Result<GribResponse> {
        val coords = "POINT($lon $lat)"
        val encodedCoords = URLEncoder.encode(coords, StandardCharsets.UTF_8.toString())
        val url = "http://158.39.75.141:5000/collections/weather_forecast/position?coords=$encodedCoords"
        return dataSource.getGribData(url)
    }
}