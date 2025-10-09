package no.uio.ifi.in2000.team47.rocketboy.ui.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.model.locationforecast.Timeseries
import no.uio.ifi.in2000.team47.rocketboy.model.ratings.getBadFactors
import no.uio.ifi.in2000.team47.rocketboy.model.ratings.getFixedRatingColor
import no.uio.ifi.in2000.team47.rocketboy.model.ratings.getRating
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LocationforecastCard(timeSeries: Timeseries, settings: WeatherSettingsData, rating: Int, modifier: Modifier = Modifier) {
    val ratingPercent = rating / 100f
    val barColor = getFixedRatingColor(rating)
    val badFactors = getBadFactors(timeSeries.data, settings)
    var expanded by remember { mutableStateOf(false) }

    val temperature = timeSeries.data.instant?.details?.airTemperature?.toString() ?: "N/A"
    val windSpeed = timeSeries.data.instant?.details?.windSpeed?.toString() ?: "N/A"
    val windFromDirection = timeSeries.data.instant?.details?.windFromDirection
    val windDirection = ((windFromDirection?.plus(180))?.rem(360))?.toInt().toString()
    val symbolCode = timeSeries.data.next1Hours?.summary?.symbolCode ?: "clearsky_day"

    val temperatureIsBad = badFactors.any { it.equals("temperature", ignoreCase = true) }
    val windSpeedIsBad = badFactors.any { it.contains("wind", ignoreCase = true) && it.contains("speed", ignoreCase = true) }

    val iconUrl = "https://api.met.no/images/weathericons/png/$symbolCode.png"

    val extraDetails = listOf(
        "Precipitation" to "${timeSeries.data.next1Hours?.details?.precipitationAmount ?: "N/A"} mm",
        "Humidity" to "${timeSeries.data.instant?.details?.relativeHumidity ?: "N/A"} %",
        "Dew Point" to "${timeSeries.data.instant?.details?.dewPointTemperature ?: "N/A"} °C",
        "Cloud Fraction" to "${timeSeries.data.instant?.details?.cloudAreaFraction ?: "N/A"} %",
        "Fog" to "${timeSeries.data.instant?.details?.fogAreaFraction ?: "N/A"} %"
    )

    val utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"))
    val norwegianFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale("no"))
    val instant = Instant.from(utcFormatter.parse(timeSeries.time))
    val formattedTime = instant.atZone(ZoneId.of("Europe/Oslo")).format(norwegianFormatter)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { expanded = !expanded }
            .semantics { contentDescription = "Weather forecast for $formattedTime with temperature $temperature °C" }, // Add contentDescription for accessibility
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RocketBoyTheme.colors.darkPrimary),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row {
                Text(
                    text = formattedTime,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = lerp(RocketBoyTheme.colors.onBackground[0],RocketBoyTheme.colors.lightPrimary, 0.6f),
                    modifier = Modifier.semantics { contentDescription = "Time of forecast: $formattedTime" } // Add contentDescription for accessibility
                )

                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Collapse forecast details" else "Expand forecast details", // Add contentDescription for accessibility
                    modifier = Modifier
                        .rotate(if (expanded) 180f else 0f)  // Rotate the arrow based on expanded state
                        .size(30.dp)
                        .clickable { expanded = !expanded },
                    tint = RocketBoyTheme.colors.onDarkPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = iconUrl,
                    contentDescription = "Weather Icon: $symbolCode", // Add contentDescription for accessibility
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$temperature °C",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (temperatureIsBad) RocketBoyTheme.colors.red else RocketBoyTheme.colors.onDarkPrimary,
                    modifier = Modifier.semantics { contentDescription = "Temperature: $temperature °C" } // Add contentDescription for accessibility
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Wind: $windSpeed m/s",
                    fontSize = 16.sp,
                    fontWeight = if (windSpeedIsBad) FontWeight.ExtraBold else FontWeight.Normal,
                    color = RocketBoyTheme.colors.onDarkPrimary,
                    modifier = Modifier.semantics { contentDescription = "Wind speed: $windSpeed m/s" } // Add contentDescription for accessibility
                )
                if (windSpeedIsBad) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Wind speed warning",
                        tint = RocketBoyTheme.colors.yellow,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Direction: $windDirection°",
                    fontSize = 16.sp,
                    color = RocketBoyTheme.colors.onDarkPrimary,
                    modifier = Modifier.semantics { contentDescription = "Wind direction: $windDirection°" } // Add contentDescription for accessibility
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.arrow_icon),
                    tint = RocketBoyTheme.colors.onDarkPrimary,
                    contentDescription = null, // No content description as it's decorative
                    modifier = Modifier
                        .rotate(windDirection.toFloat())
                        .size(16.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    extraDetails.forEach { (label, value) ->
                        val isBad = badFactors.any { it.equals(label, ignoreCase = true) }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "$label: $value",
                                fontSize = 15.sp,
                                fontWeight = if (isBad) FontWeight.ExtraBold else FontWeight.Normal,
                                color = RocketBoyTheme.colors.onDarkPrimary,
                                modifier = Modifier.semantics { contentDescription = "$label: $value" } // Add contentDescription for accessibility
                            )
                            if (isBad) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "$label warning",
                                    tint = RocketBoyTheme.colors.yellow,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (getRating(timeSeries.data, settings) == 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(RocketBoyTheme.colors.red)
                        .padding(8.dp)
                        .semantics { contentDescription = "Launch conditions not suitable for forecast" } // Add contentDescription for accessibility
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Launch conditions not suitable",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            } else {
                Text(
                    text = "Forecast Rating: $rating%",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = lerp(RocketBoyTheme.colors.onBackground[0],RocketBoyTheme.colors.lightPrimary, 0.6f),
                    modifier = Modifier.semantics { contentDescription = "Forecast rating: $rating%" } // Add contentDescription for accessibility
                )

                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RocketBoyTheme.colors.darkSecondary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (rating == 0) 0.05f else ratingPercent)
                            .background(barColor)
                            .clip(RoundedCornerShape(10.dp))
                            .height(16.dp)
                            .semantics { contentDescription = "Rating bar with $rating% rating" } // Add contentDescription for accessibility
                    )
                }
            }
        }
    }
}

