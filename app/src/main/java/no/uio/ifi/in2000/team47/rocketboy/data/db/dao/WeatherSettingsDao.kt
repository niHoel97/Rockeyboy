package no.uio.ifi.in2000.team47.rocketboy.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData

@Dao
interface WeatherSettingsDao {
    @Insert
    suspend fun insert(settings: WeatherSettingsData)

    @Query("SELECT * FROM weather_settings_table ORDER BY id DESC LIMIT 1")
    suspend fun getLatest(): WeatherSettingsData?
}
