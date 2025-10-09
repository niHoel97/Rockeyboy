package no.uio.ifi.in2000.team47.rocketboy.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_settings_table")
data class WeatherSettingsData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tempMin: Double,
    val tempMax: Double,
    val humidityMin: Double,
    val humidityMax: Double,
    val windMin: Double,
    val windMax: Double,
    val precipitationMin: Double,
    val precipitationMax: Double,
    val fogMin: Double,
    val fogMax: Double,
    val dewMin: Double,
    val dewMax: Double,
    val cloudLowMin: Double,
    val cloudLowMax: Double,
    val cloudMediumMin: Double,
    val cloudMediumMax: Double,
    val cloudHighMin: Double,
    val cloudHighMax: Double,
    val shearMin: Double,
    val shearMax: Double
)
