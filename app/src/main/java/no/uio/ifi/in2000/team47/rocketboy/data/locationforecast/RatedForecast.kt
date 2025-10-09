package no.uio.ifi.in2000.team47.rocketboy.data.locationforecast

import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.Timeseries

data class RatedForecast(
    val forecast: Timeseries,
    val settings: WeatherSettingsData,
    val rating: Int
)