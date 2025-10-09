package no.uio.ifi.in2000.team47.rocketboy.model.locationforecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse (
    @SerialName("type") val type : String,
    @SerialName("geometry") val geometry : Geometry,
    @SerialName("properties") val properties : Properties
)

@Serializable
data class Geometry (
    @SerialName("type") val type : String,
    @SerialName("coordinates") val coordinates : List<Double>
)

@Serializable
data class Properties (
    @SerialName("meta") val meta : Meta,
    @SerialName("timeseries") val timeseries : List<Timeseries>
)

@Serializable
data class Meta (
    @SerialName("updated_at") val updatedAt : String,
    @SerialName("units") val units : Units
)

@Serializable
data class Timeseries (
    @SerialName("time") val time : String,
    @SerialName("data") val data : Data
)

@Serializable
data class Data (
    @SerialName("instant") val instant : Instant? = null,
    @SerialName("next_12_hours") val next12Hours : Next_12_hours? = null,
    @SerialName("next_1_hours") val next1Hours : Next_1_hours? = null,
    @SerialName("next_6_hours") val next6Hours : Next_6_hours? = null
)

@Serializable
data class Instant (
    @SerialName("details") val details : Details
)

@Serializable
data class Next_12_hours (
    @SerialName("summary") val summary : Summary,
    @SerialName("details") val details : Details
)

@Serializable
data class Next_1_hours (
    @SerialName("summary") val summary : Summary,
    @SerialName("details") val details : Details
)

@Serializable
data class Next_6_hours (
    @SerialName("summary") val summary : Summary,
    @SerialName("details") val details : Details
)

@Serializable
data class Units (
    @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevel : String,
    @SerialName("air_temperature") val airTemperature : String,
    @SerialName("air_temperature_max") val airTemperatureMax : String,
    @SerialName("air_temperature_min") val airTemperatureMin : String,
    @SerialName("air_temperature_percentile_90") val airTemperaturePercentile90 : String,
    @SerialName("air_temperature_percentile_10") val airTemperaturePercentile10 : String,
    @SerialName("cloud_area_fraction") val cloudAreaFraction : String,
    @SerialName("cloud_area_fraction_high") val cloudAreaFractionHigh : String,
    @SerialName("cloud_area_fraction_low") val cloudAreaFractionLow : String,
    @SerialName("cloud_area_fraction_medium") val cloudAreaFractionMedium : String,
    @SerialName("dew_point_temperature") val dewPointTemperature : String,
    @SerialName("fog_area_fraction") val fogAreaFraction : String,
    @SerialName("precipitation_amount") val precipitationAmount : String,
    @SerialName("precipitation_amount_max") val precipitationAmountMax: String,
    @SerialName("precipitation_amount_min") val precipitationAmountMin: String,
    @SerialName("probability_of_precipitation") val probabilityOfPrecipitation: String,
    @SerialName("probability_of_thunder") val probabilityOfThunder: String,
    @SerialName("relative_humidity") val relativeHumidity : String,
    @SerialName("ultraviolet_index_clear_sky") val ultravioletIndexClearSky : Int,
    @SerialName("wind_from_direction") val windFromDirection : String,
    @SerialName("wind_speed") val windSpeed : String,
    @SerialName("wind_speed_of_gust") val windSpeedOfGust : String,
    @SerialName("wind_speed_percentile_10") val windSpeedPercentile10 : String,
    @SerialName("wind_speed_percentile_90") val windSpeedPercentile90 : String
)

@Serializable
data class Summary (
    @SerialName("symbol_code") val symbolCode : String
)

@Serializable
data class Details (
    @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevel : Double? = null,
    @SerialName("air_temperature") val airTemperature : Double? = null,
    @SerialName("air_temperature_max") val airTemperatureMax : Double? = null,
    @SerialName("air_temperature_min") val airTemperatureMin : Double? = null,
    @SerialName("air_temperature_percentile_90") val airTemperaturePercentile90 : Double? = null,
    @SerialName("air_temperature_percentile_10") val airTemperaturePercentile10 : Double? = null,
    @SerialName("cloud_area_fraction") val cloudAreaFraction : Double? = null,
    @SerialName("cloud_area_fraction_high") val cloudAreaFractionHigh : Double? = null,
    @SerialName("cloud_area_fraction_low") val cloudAreaFractionLow : Double? = null,
    @SerialName("cloud_area_fraction_medium") val cloudAreaFractionMedium : Double? = null,
    @SerialName("dew_point_temperature") val dewPointTemperature : Double? = null,
    @SerialName("fog_area_fraction") val fogAreaFraction : Double? = null,
    @SerialName("precipitation_amount") val precipitationAmount : Double? = null,
    @SerialName("precipitation_amount_max") val precipitationAmountMax: Double? = null,
    @SerialName("precipitation_amount_min") val precipitationAmountMin: Double? = null,
    @SerialName("probability_of_precipitation") val probabilityOfPrecipitation: Double? = null,
    @SerialName("probability_of_thunder") val probabilityOfThunder: Double? = null,
    @SerialName("relative_humidity") val relativeHumidity : Double? = null,
    @SerialName("ultraviolet_index_clear_sky") val ultravioletIndexClearSky : Double? = null,
    @SerialName("wind_from_direction") val windFromDirection : Double? = null,
    @SerialName("wind_speed") val windSpeed : Double? = null,
    @SerialName("wind_speed_of_gust") val windSpeedOfGust : Double? = null,
    @SerialName("wind_speed_percentile_10") val windSpeedPercentile10 : Double? = null,
    @SerialName("wind_speed_percentile_90") val windSpeedPercentile90 : Double? = null
)






