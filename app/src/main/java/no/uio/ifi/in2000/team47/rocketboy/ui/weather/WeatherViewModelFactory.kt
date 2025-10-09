package no.uio.ifi.in2000.team47.rocketboy.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribRepository
import no.uio.ifi.in2000.team47.rocketboy.data.locationforecast.LocationforecastRepository

class WeatherViewModelFactory(
    private val locationforecastRepository: LocationforecastRepository,
    private val gribRepository: GribRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(locationforecastRepository, gribRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}