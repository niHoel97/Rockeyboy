package no.uio.ifi.in2000.team47.rocketboy.ui.weather

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribRepository
import no.uio.ifi.in2000.team47.rocketboy.data.locationforecast.LocationforecastRepository
import no.uio.ifi.in2000.team47.rocketboy.data.settings.CoordinatesData
import no.uio.ifi.in2000.team47.rocketboy.model.grib.GribResponse
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.WeatherResponse
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

var isInitialized = mutableStateOf(false)

class WeatherViewModel(
    private val locationForecastRepository: LocationforecastRepository,
    private val gribRepository: GribRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _gribResponseState = MutableStateFlow<GribResponse?>(null)
    val gribResponseState: StateFlow<GribResponse?> = _gribResponseState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _instantAirPressure = MutableStateFlow<Double>(Double.MAX_VALUE)
    val instantAirPressure: StateFlow<Double> = _instantAirPressure

    private val _coordinates = MutableStateFlow<CoordinatesData>(CoordinatesData(59.9441020, 10.7191405, 60))
    val coordinates: StateFlow<CoordinatesData> = _coordinates

    fun updateCoordinates(lat: Double, lon: Double, alt: Int){
        _coordinates.value = CoordinatesData(lat,lon,alt)
        fetchWeatherAndGribData(lat,lon,alt)

    }

    fun fetchWeatherAndGribData(lat: Double, lon: Double, alt: Int) {
        viewModelScope.launch {
            try {
                // Fetch weather-data
                val weatherResult = locationForecastRepository.fetchWeather(coordinates.value.lat, coordinates.value.lon, coordinates.value.alt)
                if (weatherResult != null) {
                    _weatherState.value = weatherResult
                    _instantAirPressure.value =
                        weatherResult.properties.timeseries.firstOrNull()?.data?.instant?.details?.airPressureAtSeaLevel
                            ?: Double.MAX_VALUE
                } else {
                    _errorMessage.value = "Error fetching weather data"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
            try {
                // Fetch Grib-data
                val gribResult = gribRepository.fetchGrib(coordinates.value.lat, coordinates.value.lon)
                if (gribResult.isSuccess) {
                    _gribResponseState.value = gribResult.getOrNull()
                } else {
                    _errorMessage.value = "Error fetching Grib data: ${gribResult.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    init {
        if (!isInitialized.value) {
            isInitialized.value = true
            viewModelScope.launch {
                // Initial fetch at app launch
                fetchWeatherAndGribData(
                    _coordinates.value.lat,
                    _coordinates.value.lon,
                    _coordinates.value.alt
                )
                while (isActive) {
                    // Compute delay until next 3-hour interval (UTC)
                    val nowUtc = Instant.now().atZone(ZoneOffset.UTC)
                    val currentHour = nowUtc.hour
                    val nextIntervalHour = ((currentHour / 3 + 1) * 3) % 24
                    var nextInterval = nowUtc
                        .withHour(nextIntervalHour)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0)
                    // If the calculated interval is not after now, advance to the next day
                    if (!nextInterval.isAfter(nowUtc)) {
                        nextInterval = nextInterval.plusDays(1)
                    }
                    val delayMillis = Duration.between(nowUtc.toInstant(), nextInterval.toInstant()).toMillis() +
                        Duration.ofMinutes(1).toMillis()
                    Log.d("WeatherViewModel", "Hours to next fetch: ${(delayMillis.toFloat() / 1000 / 60 / 60)}\nIn millis: $delayMillis")
                    //Waiting calculated time
                    delay(delayMillis)
                    // Fetch after waiting
                    fetchWeatherAndGribData(
                        _coordinates.value.lat,
                        _coordinates.value.lon,
                        _coordinates.value.alt
                    )
                    Log.d("WeatherViewModel", "Called fetchWeatherAndGribData(${_coordinates.value.lat},${_coordinates.value.lon},${_coordinates.value.alt})")
                }
            }
        }
    }
}






