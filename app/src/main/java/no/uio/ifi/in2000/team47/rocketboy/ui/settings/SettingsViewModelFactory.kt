package no.uio.ifi.in2000.team47.rocketboy.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.uio.ifi.in2000.team47.rocketboy.data.db.repository.WeatherSettingsRepository

class SettingsViewModelFactory(
    private val weatherSettingsRepository: WeatherSettingsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(weatherSettingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

