package no.uio.ifi.in2000.team47.rocketboy.model.ratings

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.Data
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

fun getRating(data: Data, settings: WeatherSettingsData): Int{
    val MAX_SCORE: Double = 100.toDouble()/7

    val temp = rate(settings.tempMin, settings.tempMax, 24.0, data.instant?.details?.airTemperature, MAX_SCORE)
    val humidity = rate(settings.humidityMin, settings.humidityMax, 0.0, data.instant?.details?.relativeHumidity, MAX_SCORE)
    val windspeed = rate(settings.windMin, settings.windMax, 0.0, data.instant?.details?.windSpeed, MAX_SCORE)
    val precipitation = rate(settings.precipitationMin, settings.precipitationMax, 0.0, data.next1Hours?.details?.precipitationAmount, MAX_SCORE)
    val fog = rate(settings.fogMin, settings.fogMax, 0.0, data.instant?.details?.fogAreaFraction, MAX_SCORE)
    val dew = rate(settings.dewMin, settings.dewMax, 3.0, data.instant?.details?.dewPointTemperature, MAX_SCORE)
    val cloud = rateCloudArea(settings.cloudLowMax, settings.cloudMediumMax, settings.cloudHighMax, data.instant?.details?.cloudAreaFractionLow, data.instant?.details?.cloudAreaFractionMedium, data.instant?.details?.cloudAreaFractionHigh, MAX_SCORE)

    if(temp == 0.0 || humidity == 0.0 ||windspeed == 0.0 || precipitation == 0.0 || fog == 0.0 || dew == 0.0 || cloud == 0.0) return 0
    else return (temp+humidity+windspeed+precipitation+fog+dew+cloud).roundToInt()
}

fun rate(minVal: Double, maxVal: Double, optimal: Double, value: Double?, maxScore: Double): Double{
    if(value == null) return 5.0
    if(value < minVal || value > maxVal) return 0.0

    val rangeSpan: Double = maxVal - minVal

    if(rangeSpan == 0.0) return if(value == optimal) maxScore else 0.0

    val distance: Double = abs(value.minus(optimal))
    val maxDistance: Double = max(abs(minVal - optimal), abs(maxVal - optimal))

    if(maxDistance == 0.0) return maxScore

    var score: Double = (1 - (distance/maxDistance))*maxScore
    score = max(0.1, score)

    score = String.format("%.1f", score).replace(",", ".").toDouble()

    return score
}

fun getBadFactors(data: Data, settings: WeatherSettingsData): List<String> {
    val MAX_SCORE = 100.0 / 7

    val badFactors = mutableListOf<String>()

    if (rate(settings.tempMin, settings.tempMax, 24.0, data.instant?.details?.airTemperature, MAX_SCORE) == 0.0) badFactors.add("Temperature")
    if (rate(settings.humidityMin, settings.humidityMax, 0.0, data.instant?.details?.relativeHumidity, MAX_SCORE) == 0.0) badFactors.add("Humidity")
    if (rate(settings.windMin, settings.windMax, 0.0, data.instant?.details?.windSpeed, MAX_SCORE) == 0.0) badFactors.add("Wind Speed")
    if (rate(settings.precipitationMin, settings.precipitationMax, 0.0, data.next1Hours?.details?.precipitationAmount, MAX_SCORE) == 0.0) badFactors.add("Precipitation")
    if (rate(settings.fogMin, settings.fogMax, 0.0, data.instant?.details?.fogAreaFraction, MAX_SCORE) == 0.0) badFactors.add("Fog")
    if (rate(settings.dewMin, settings.dewMax, 3.0, data.instant?.details?.dewPointTemperature, MAX_SCORE) == 0.0) badFactors.add("Dew Point")
    if (rateCloudArea(settings.cloudLowMax, settings.cloudMediumMax, settings.cloudHighMax, data.instant?.details?.cloudAreaFractionLow, data.instant?.details?.cloudAreaFractionMedium, data.instant?.details?.cloudAreaFractionHigh, MAX_SCORE) == 0.0) badFactors.add("Cloud fraction")

    return badFactors
}


fun rateCloudArea(max_low: Double, max_med: Double, max_high: Double, val_low: Double?, val_med: Double?, val_high: Double?, maxScore: Double): Double{
    val low = rate(0.0, max_low, 0.0, val_low, maxScore/3)
    val med = rate(0.0, max_med, 0.0, val_med, maxScore/3)
    val high = rate(0.0, max_high, 0.0, val_high, maxScore/3)

    return if(low == 0.0 || med == 0.0 || high == 0.0) 0.0 else low+med+high
}


@Composable
fun getFixedRatingColor(rating: Int): Color {
    return when (rating) {
        in 70..100 -> RocketBoyTheme.colors.green
        in 40..69 -> RocketBoyTheme.colors.yellow
        else -> RocketBoyTheme.colors.red
    }
}
