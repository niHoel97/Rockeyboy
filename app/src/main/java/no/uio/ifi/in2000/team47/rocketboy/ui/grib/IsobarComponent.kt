package no.uio.ifi.in2000.team47.rocketboy.ui.grib

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme

enum class IsobarComponentDesign {Light, Dark}


@Composable
fun IsobarComponent(
    modifier: Modifier = Modifier,
    design: IsobarComponentDesign = IsobarComponentDesign.Dark,
    index: Int = 0,
    isHeader: Boolean? = false,
    altitude: Int? = null,
    hPa: Int? = null,
    windDirection: Double? = null,
    windMagnitude: Double? = null,
    @DrawableRes directionIconRes: Int? = null
) {
    val backgroundColor = if (design == IsobarComponentDesign.Dark) lerp(RocketBoyTheme.colors.background[1],
        RocketBoyTheme.colors.darkPrimary, index.toFloat().div(10) + 0.5f) else lerp(RocketBoyTheme.colors.background[0],
        RocketBoyTheme.colors.lightPrimary, index.toFloat().div(10))
    val arrowColor = if (design == IsobarComponentDesign.Light) lerp(RocketBoyTheme.colors.background[1],
        RocketBoyTheme.colors.darkPrimary, index.toFloat().div(10) + 0.5f) else lerp(RocketBoyTheme.colors.background[0],
        RocketBoyTheme.colors.lightPrimary, index.toFloat().div(10))
    val headerBackground = Brush.verticalGradient(listOf(lerp(RocketBoyTheme.colors.background[0],
        RocketBoyTheme.colors.background[1], 0.09f),backgroundColor))
    val contentColor = if (design == IsobarComponentDesign.Dark) RocketBoyTheme.colors.onDarkPrimary else RocketBoyTheme.colors.onLightPrimary

    var size = remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val showDirection = remember(size.value) {
        with(density) { size.value.width.toDp() > 280.dp }
    }
    val showHPa = remember(size.value) {
        with(density) { size.value.width.toDp() > 220.dp }
    }
    val showWindMagnitude = remember(size.value) {
        with(density) { size.value.width.toDp() > 185.dp }
    }

    Row(
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth()
            .onSizeChanged { size.value = it }
            .semantics {
                contentDescription = "Isobar component showing altitude, pressure, wind direction, and wind speed"
            }
    ) {
        // Container with content
        Row(
            modifier = Modifier
                .background(backgroundColor)
                .background(if (isHeader == true) headerBackground else Brush.verticalGradient(listOf(backgroundColor, backgroundColor)))
                .fillMaxHeight()
                .weight(1f)
                .padding(vertical = 0.dp)
                .absolutePadding(left = 15.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                if (isHeader == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Isobars",
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            color = contentColor,
                            maxLines = 1,
                            modifier = Modifier.semantics {
                                contentDescription = "Isobar header"
                            }
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Altitude box
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = altitude?.let { "${it}m" } ?: if(isHeader == true) "Altitude" else "?",
                                color = contentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Left,
                                maxLines = 1,
                                modifier = Modifier
                                    .width(64.dp)
                                    .semantics {
                                        contentDescription = "Altitude: ${altitude?.let { "$it m" } ?: "unknown"}"
                                    }
                            )
                        }
                    }

                    // hPa box
                    if (showHPa){
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hPa?.let { "${it}hPa" } ?: if(isHeader == true) "Pressure" else "?",
                                    color = contentColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Left,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .width(64.dp)
                                        .semantics {
                                            contentDescription = "Pressure: ${hPa?.let { "$it hPa" } ?: "unknown"}"
                                        }
                                )
                            }
                        }
                    }

                    //Direction box
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .height(26.38.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (directionIconRes != null && isHeader == false) Box(
                                modifier = Modifier
                                    .size(width = 9.dp, height = 19.dp)
                                    .semantics {
                                        contentDescription = "Wind direction icon"
                                    }
                            ) {
                                Icon(
                                    painter = painterResource(id = directionIconRes),
                                    contentDescription = windDirection?.let { "Wind direction icon rotated at ${it.toInt()}°" } ?: "Wind direction icon",
                                    modifier = Modifier.rotate((windDirection ?: 0.0).toFloat()),
                                    tint = arrowColor
                                )
                            }
                            if (showDirection)
                                Text(
                                    text = windDirection?.let { "${it.toInt()}°" } ?: if(isHeader == true) "Direction" else "?",
                                    color = contentColor,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Left,
                                    maxLines = 1,
                                    modifier = Modifier.semantics {
                                        contentDescription = "Wind direction: ${windDirection?.toInt() ?: "unknown"} degrees"
                                    }
                                )
                        }
                    }

                    //Magnitude box
                    if(showWindMagnitude){
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = windMagnitude?.let { "${it}m/s" } ?: if(isHeader == true) "Speed" else "?",
                                    color = contentColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Left,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .width(65.dp)
                                        .semantics {
                                            contentDescription = "Wind speed: ${windMagnitude?.let { "$it m/s" } ?: "unknown"}"
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(widthDp = 500, heightDp = 52)
@Composable
fun IsobarComponentPreviewWhite() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            IsobarComponent(
                isHeader = false,
                altitude = 15000,
                hPa = 100,
                windDirection = 256.0,
                windMagnitude = 7.52,
                directionIconRes = R.drawable.arrow_icon
            )
        }
    }
}