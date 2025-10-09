package no.uio.ifi.in2000.team47.rocketboy.ui.grib

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribData
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@Composable
fun WindAnalysisCard(
    modifier: Modifier = Modifier,
    gribDataList: List<GribData>,
    settings: WeatherSettingsData,
) {
    // Calculating average wind vector (u,v components)
    val avgWindComponents: Pair<Double, Double> = remember(gribDataList) {
        val (sumU, sumV) = gribDataList.fold(0.0 to 0.0) { (accU, accV), data ->
            val rad = Math.toRadians(data.windDirection)
            val u = data.windSpeed * cos(rad)
            val v = data.windSpeed * sin(rad)
            (accU + u) to (accV + v)
        }
        sumU / gribDataList.size to sumV / gribDataList.size
    }
    val avgU = avgWindComponents.first
    val avgV = avgWindComponents.second
    val avgVectorSpeed: Double = sqrt(avgU * avgU + avgV * avgV)
    val avgVectorDirectionDeg: Double = remember(gribDataList) {
        (toDegrees(atan2(avgV, avgU)) + 360) % 360
    }

    // Map degrees to cardinal directions
    fun degreesToCardinal(deg: Double): String = when (deg) {
        in 337.5..360.0, in 0.0..22.5 -> "north"
        in 22.5..67.5 -> "north-east"
        in 67.5..112.5 -> "east"
        in 112.5..157.5 -> "south-east"
        in 157.5..202.5 -> "south"
        in 202.5..247.5 -> "south-west"
        in 247.5..292.5 -> "west"
        in 292.5..337.5 -> "north-west"
        else -> ""
    }

    val avgVectorCardinal = degreesToCardinal(avgVectorDirectionDeg)

    // Classify average wind speed into low, normal, or high
    val windCategory: String = when {
        avgVectorSpeed < 5.0 -> "low"
        avgVectorSpeed <= 10.0 -> "medium"
        else -> "high"
    }

    Card(
        modifier = modifier.fillMaxWidth().semantics{contentDescription = "Wind Analysis: average wind details and wind category"},
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
        elevation = CardDefaults.elevatedCardElevation(20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .background(color = Color(0xFFD9D9D9))
                    .padding(vertical = 15.dp)
                    .semantics {
                        contentDescription = "Wind analysis section with wind direction and average speed"
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Wind analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.5.sp,
                    modifier = Modifier.semantics { contentDescription = "Wind analysis title" }
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(RocketBoyTheme.colors.lightPrimary),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_icon),
                            tint = RocketBoyTheme.colors.darkPrimary,
                            contentDescription = "Arrow indicating average wind direction",
                            modifier = Modifier
                                .rotate(avgVectorDirectionDeg.toFloat())
                                .size(50.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Average wind",
                            color = RocketBoyTheme.colors.darkPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.semantics { contentDescription = "Average wind label" }
                        )
                        Text(
                            "${"%.2f".format(avgVectorDirectionDeg)}°",
                            modifier = Modifier.semantics { contentDescription = "Wind direction in degrees" }
                        )
                        Text(
                            "${"%.1f".format(avgVectorSpeed)} m/s",
                            modifier = Modifier.semantics { contentDescription = "Average wind speed" }
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 15.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.list_ellipse),
                                contentDescription = "List icon",
                                tint = RocketBoyTheme.colors.darkPrimary
                            )
                        }
                        Column(
                            modifier = Modifier
                                .absolutePadding(left = 15.dp)
                        ) {
                            Text(
                                buildAnnotatedString {
                                    append("Wind averages towards ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(avgVectorCardinal)
                                    }
                                    append(" at ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("%.1f m/s".format(avgVectorSpeed))
                                    }
                                },
                                modifier = Modifier.semantics { contentDescription = "Average wind direction and speed description" }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.list_ellipse),
                                contentDescription = "List icon",
                                tint = RocketBoyTheme.colors.darkPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .absolutePadding(left = 15.dp)
                        ) {
                            Text(
                                buildAnnotatedString {
                                    append("Wind is in range ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(windCategory)
                                    }
                                },
                                modifier = Modifier.semantics { contentDescription = "Wind speed category" }
                            )
                        }
                    }
                }
            }
        }
    }
}
