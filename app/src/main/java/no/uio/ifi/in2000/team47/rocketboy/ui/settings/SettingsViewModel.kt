package no.uio.ifi.in2000.team47.rocketboy.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.data.db.repository.WeatherSettingsRepository


class SettingsViewModel(
    private val weatherSettingsRepository: WeatherSettingsRepository
) : ViewModel() {

    private val _weatherSettings = MutableStateFlow<WeatherSettingsData?>(null)
    val weatherSettings: StateFlow<WeatherSettingsData?> = _weatherSettings

    init {
        fetchWeatherSettings()
    }

    private fun fetchWeatherSettings() {
        viewModelScope.launch {
            _weatherSettings.value = weatherSettingsRepository.getWeatherSettings() ?: defaultWeatherSettings()
        }
    }

    fun resetWeatherSettingsToDefault() {
        viewModelScope.launch {
            val defaultSettings = defaultWeatherSettings()
            weatherSettingsRepository.insertWeatherSettings(defaultSettings)
            _weatherSettings.value = defaultSettings
        }
    }

    private fun defaultWeatherSettings(): WeatherSettingsData {
        return WeatherSettingsData(
            tempMin = 0.0,
            tempMax = 35.0,
            humidityMin = 0.0,
            humidityMax = 75.0,
            windMin = 0.0,
            windMax = 8.5,
            precipitationMin = 0.0,
            precipitationMax = 0.0,
            fogMin = 0.0,
            fogMax = 0.0,
            dewMin = -3.0,
            dewMax = 15.0,
            cloudLowMin = 0.0,
            cloudLowMax = 8.5,
            cloudMediumMin = 0.0,
            cloudMediumMax = 15.0,
            cloudHighMin = 0.0,
            cloudHighMax = 15.0,
            shearMin = 0.0,
            shearMax = 24.5
        )
    }

    fun updateWeatherSettings(newSettings: WeatherSettingsData) {
        viewModelScope.launch {
            weatherSettingsRepository.insertWeatherSettings(newSettings)
            _weatherSettings.value = newSettings // Trigge UI-observasjon
        }
    }

}

