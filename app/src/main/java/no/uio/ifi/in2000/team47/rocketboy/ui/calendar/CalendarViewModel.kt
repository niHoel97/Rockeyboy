package no.uio.ifi.in2000.team47.rocketboy.ui.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.data.locationforecast.LocationforecastRepository
import no.uio.ifi.in2000.team47.rocketboy.data.settings.CoordinatesData
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.Timeseries
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.WeatherResponse
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@RequiresApi(Build.VERSION_CODES.O)
class CalendarViewModel(
    private val repository: LocationforecastRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _displayedMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    val displayedMonth: StateFlow<LocalDate> = _displayedMonth

    private val _datesWithData = MutableStateFlow(setOf<LocalDate>())
    val datesWithData: StateFlow<Set<LocalDate>> = _datesWithData

    private val _selectedForecasts = MutableStateFlow<List<Timeseries>>(emptyList())
    val selectedForecasts: StateFlow<List<Timeseries>> = _selectedForecasts

    private val _coordinates = MutableStateFlow<CoordinatesData>(CoordinatesData(60.0, 10.0, 60))
    val coordinates: StateFlow<CoordinatesData> = _coordinates

    private var latestWeather: WeatherResponse? = null

    init {
        fetchTemperature()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        fetchTemperature()
    }

    fun changeMonth(by: Long) {
        _displayedMonth.value = _displayedMonth.value.plusMonths(by)
    }

    private fun fetchTemperature() {
        viewModelScope.launch {
            val response = repository.fetchWeather(coordinates.value.lat, coordinates.value.lon, coordinates.value.alt)
            latestWeather = response

            _datesWithData.value = response?.properties?.timeseries
                ?.mapNotNull {
                    try {
                        ZonedDateTime.parse(it.time).withZoneSameInstant(ZoneId.of("Europe/Oslo")).toLocalDate()
                    } catch (e: Exception) {
                        null
                    }
                }?.toSet() ?: emptySet()

            val selected = _selectedDate.value

            val forecastsForDate = response?.properties?.timeseries?.filter {
                try {
                    ZonedDateTime.parse(it.time).toLocalDate() == selected
                } catch (e: Exception) {
                    false
                }
            } ?: emptyList()

            _selectedForecasts.value = forecastsForDate
            updateForecastsForDate()
        }
    }

    fun observeWeather(weatherFlow: StateFlow<WeatherResponse?>) {
        viewModelScope.launch {
            weatherFlow.collectLatest { weather ->
                weather?.let {
                    latestWeather = it
                    updateDatesWithData()
                    updateForecastsForDate()
                }
            }
        }
    }

    private fun updateDatesWithData() {
        latestWeather?.properties?.timeseries?.mapNotNull {
            try {
                ZonedDateTime.parse(it.time).toLocalDate()
            } catch (e: Exception) {
                null
            }
        }?.toSet()?.let { _datesWithData.value = it }
    }

    private fun updateForecastsForDate() {
        val selected = _selectedDate.value
        val forecastsForDate = latestWeather?.properties?.timeseries?.filter {
            try {
                val zonedDateTime = ZonedDateTime.parse(it.time).withZoneSameInstant(ZoneId.of("Europe/Oslo"))
                val forecastDate = zonedDateTime.toLocalDate()
                val forecastHour = zonedDateTime.hour
                forecastDate == selected && forecastHour in 0..23
            } catch (e: Exception) {
                false
            }
        } ?: emptyList()
        _selectedForecasts.value = forecastsForDate
    }
}
