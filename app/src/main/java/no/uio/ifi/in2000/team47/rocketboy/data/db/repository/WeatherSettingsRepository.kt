package no.uio.ifi.in2000.team47.rocketboy.data.db.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team47.rocketboy.data.db.dao.WeatherSettingsDao
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData

class WeatherSettingsRepository(private val dao: WeatherSettingsDao) {

    suspend fun insertWeatherSettings(settings: WeatherSettingsData) {
        withContext(Dispatchers.IO) {
            dao.insert(settings)
        }
    }

    suspend fun getWeatherSettings(): WeatherSettingsData? {
        return withContext(Dispatchers.IO) {
            dao.getLatest()
        }
    }
}
