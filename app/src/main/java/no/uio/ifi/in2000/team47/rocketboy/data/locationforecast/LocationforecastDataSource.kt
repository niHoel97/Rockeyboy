package no.uio.ifi.in2000.team47.rocketboy.data.locationforecast

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.WeatherResponse

class LocationforecastDataSource(private val httpClient: HttpClient) {
    suspend fun getWeatherForecast(url: String): WeatherResponse? {
        return try {
            val response = httpClient.get(url)
            Log.d("LocationforecastDataSource", "Response: ${response.status}, Body: ${response.bodyAsText()}")
            response.body<WeatherResponse>()
        } catch (e: Exception) {
            Log.e("LocationforecastDataSource", "Error getting weather forecast: ${e.message}")
            null
        }
    }
}