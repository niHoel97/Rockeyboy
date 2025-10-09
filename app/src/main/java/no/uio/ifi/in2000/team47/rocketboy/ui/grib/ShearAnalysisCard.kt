package no.uio.ifi.in2000.team47.rocketboy.ui.grib

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribData
import no.uio.ifi.in2000.team47.rocketboy.model.grib.convertToDisplayGribData
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import java.lang.Math.toDegrees
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun ShearAnalysisCard(
    modifier: Modifier = Modifier,
    gribDataList: List<GribData>,
    settings: WeatherSettingsData,
){
    val lightBlueColor = Color(0xFF8EBCFF)
    val darkBlueColor = Color(0xFF073997)

    val color = RocketBoyTheme.colors

    // Shear data
    val displayData = remember(gribDataList) { convertToDisplayGribData(gribDataList).asReversed() }

    // calculating the highest shear
    val topShearMagnitude: Double = convertToDisplayGribData(gribDataList).maxOfOrNull { it.shearMagnitude } ?: 0.0

    // Counting shears exceeding set max
    val alertCount = displayData.count {it.shearMagnitude >= settings.shearMax}

    // Extracting top shear
    val topShear = displayData.find { it.shearMagnitude == topShearMagnitude }

    // Normalize top shear direction to [0, 360)
    val topShearDirNorm: Double = topShear?.shearDirection?.let { (it + 360 ) % 360 } ?: 0.0

    // Calculating average shear direction (circular mean)
    val avgDirectionDeg: Double = remember(displayData) {
        val radians = displayData.map { Math.toRadians(it.shearDirection) }
        val x = radians.sumOf { cos(it) }
        val y = radians.sumOf { sin(it) }
        val angle = toDegrees(atan2(y, x))
        (angle + 360) % 360
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

    val avgDirectionCardinal = degreesToCardinal(avgDirectionDeg)
    val topShearCardinal = topShear?.shearDirection?.let { degreesToCardinal(( it + 360 ) % 360 )} ?: "unknown"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
        elevation = CardDefaults.elevatedCardElevation(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .background(Color(0xFFD9D9D9))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(color = Color(0xFFD9D9D9))
                        .padding(vertical = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Shear analysis",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.5.sp
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (alertCount > 0) Image(
                            painter = painterResource(id = R.drawable.alert_icon_large),
                            contentDescription = null
                        )
                        else Icon(
                            painter = painterResource(id = R.drawable.check_green),
                            contentDescription = null,
                            tint = RocketBoyTheme.colors.green
                        )
                        Text(
                            text = buildAnnotatedString {
                                if (alertCount == 0) {
                                    append("All shear magnitudes are ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("within set range")
                                    }
                                } else if (alertCount == 1) {
                                    append("Top wind shear is ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("outside set range")
                                    }
                                } else {
                                    append("Multiple shear magnitudes are ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("outside set range")
                                    }
                                }
                            },
                            textAlign = TextAlign.Left
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 15.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Max shear",
                        color = RocketBoyTheme.colors.darkPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .absolutePadding(top = 10.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_icon),
                        contentDescription = null,
                        tint = RocketBoyTheme.colors.darkPrimary,
                        modifier = Modifier.rotate(topShearDirNorm.toFloat())
                    )
                    Text(
                        text = topShear?.let { "${"%.2f".format((it.shearDirection + 360) % 360)}°" } ?: "?",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Left,
                        maxLines = 1
                    )
                    Text(
                        text = topShear?.let { "${"%.2f".format(it.shearMagnitude)}m/s" } ?: "?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Left,
                        maxLines = 1
                    )
                }
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Row{
                            Icon(
                                painter = painterResource(id = R.drawable.list_ellipse),
                                contentDescription = null,
                                tint = RocketBoyTheme.colors.darkPrimary
                            )
                        }
                        Box(
                            modifier = Modifier.absolutePadding(left = 15.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("Top shear is directed ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("${topShearCardinal}ward")
                                    }
                                }
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
                        Box {
                            Icon(
                                painter = painterResource(id = R.drawable.list_ellipse),
                                contentDescription = null,
                                tint = RocketBoyTheme.colors.darkPrimary
                            )
                        }
                        Box(
                            modifier = Modifier.absolutePadding(left = 15.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("Shear averages towards ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(avgDirectionCardinal)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
