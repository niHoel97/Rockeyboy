package no.uio.ifi.in2000.team47.rocketboy.data.locationforecast

import android.util.Log
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.WeatherResponse

class LocationforecastRepository(private val dataSource: LocationforecastDataSource) {
    suspend fun fetchWeather(lat: Double, lon: Double, alt: Int): WeatherResponse? {

        val url = "https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lon&altitude=$alt"

        return try {
            dataSource.getWeatherForecast(url)
        } catch (e: Exception) {
            Log.e("LocationforecastRepository", "Error fetching weather forecast: ${e.message}")
            null
        }
    }
}