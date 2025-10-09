package no.uio.ifi.in2000.team47.rocketboy.data.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherRange(
    val start: Double,
    val end: Double
) : Parcelable
