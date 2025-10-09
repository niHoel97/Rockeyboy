package no.uio.ifi.in2000.team47.rocketboy.ui.grib.wind_share


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribData
import no.uio.ifi.in2000.team47.rocketboy.model.grib.convertToDisplayGribData
import no.uio.ifi.in2000.team47.rocketboy.ui.grib.IsobarComponent
import no.uio.ifi.in2000.team47.rocketboy.ui.grib.IsobarComponentDesign
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme

@Composable
fun ShearTable(
    modifier: Modifier = Modifier,
    gribDataList: List<GribData>,
    settings: WeatherSettingsData
) {
    // calculating the highest shear
    val topShearMagnitude: Double = convertToDisplayGribData(gribDataList).maxOfOrNull { it.shearMagnitude } ?: 0.0

    // Reverse input and display order
    val reversedGribData = remember(gribDataList) { gribDataList.asReversed() }
    val displayData     = remember(gribDataList) { convertToDisplayGribData(gribDataList).asReversed() }

    // Table dimensions and state
    var shearFraction by rememberSaveable { mutableFloatStateOf(0.7f) }
    val rowHeight     = 52.dp
    val density       = LocalDensity.current

    @Suppress("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val totalWidth = maxWidth
        val isobarWidth = totalWidth * (1f - shearFraction)
        val shearWidth = totalWidth * shearFraction

        // Scrollable container with horizontal drag to resize
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        val totalWidthPx = with(density) { totalWidth.toPx() }
                        val minFraction = 0.2f
                        val maxFraction = 0.7f
                        val rawFraction = shearFraction - (dragAmount / totalWidthPx)
                        shearFraction = rawFraction.coerceIn(minFraction, maxFraction)
                    }
                }
                .semantics {
                    contentDescription =
                        "Interactive shear table that allows horizontal resizing of columns"
                }
        ) {
            // Wrap columns in inner Row to fill width
            Row(Modifier.fillMaxWidth()) {
                // ISOBAR COLUMN
                Column(
                    modifier = Modifier
                        .width(isobarWidth)
                        .zIndex(0f)
                        .clipToBounds()
                        .semantics {
                            contentDescription =
                                "Isobar column showing pressure levels and wind direction"
                        }
                ) {
                    IsobarComponent(
                        design          = IsobarComponentDesign.Light,
                        isHeader        = true,
                        index           = 0,
                        directionIconRes = R.drawable.arrow_icon,
                        modifier = Modifier
                            .height(rowHeight)
                            .semantics { contentDescription = "Isobar header for wind direction" }
                    )
                    reversedGribData.forEachIndexed { index, data ->
                        val windDirection = if (data.windDirection < 0) data.windDirection + 360
                                            else data.windDirection
                        IsobarComponent(
                            altitude        = roundToNearestHundred(data.moh),
                            hPa             = data.isobar,
                            windDirection   = windDirection,
                            windMagnitude   = data.windSpeed,
                            design          = if (index % 2 == 0) IsobarComponentDesign.Dark
                                              else IsobarComponentDesign.Light,
                            index           = index + 1,
                            directionIconRes = R.drawable.arrow_icon,
                            modifier = Modifier
                                .height(rowHeight)
                                .semantics {
                                    contentDescription =
                                        "Isobar row showing pressure and wind direction"
                                }
                        )
                    }
                }

                // SHEAR COLUMN
                Box(
                    modifier = Modifier
                        .width(shearWidth)
                        .fillMaxHeight()
                        .semantics {
                            contentDescription =
                                "Shear column showing shear direction and magnitude"
                        }
                ) {
                    val rowPx = with(density) { rowHeight.roundToPx() }

                    // Extra top stripe to cover half-offset shear arrow
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0,0) }
                            .height(rowHeight)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(listOf(lerp(RocketBoyTheme.colors.background[0],
                                    RocketBoyTheme.colors.background[1], 0.09f),lerp(RocketBoyTheme.colors.background[0],
                                    RocketBoyTheme.colors.lightPrimary, 0.0f)))
                            )
                            .semantics { contentDescription = "Top shear background stripe" }
                    )

                    // Draw alternating stripe backgrounds
                    displayData.forEachIndexed { index, _ ->
                        val newIndex = index + 1
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(0, newIndex * rowPx) }
                                .height(rowHeight)
                                .fillMaxWidth()
                                .background(
                                    if (newIndex % 2 != 0) lerp(RocketBoyTheme.colors.background[1],
                                        RocketBoyTheme.colors.darkPrimary, (newIndex).toFloat().div(10) + 0.5f)
                                    else lerp(RocketBoyTheme.colors.background[0],
                                        RocketBoyTheme.colors.lightPrimary, (newIndex).toFloat().div(10))
                                )
                                .semantics {
                                    contentDescription =
                                        "Shear row $index with alternating background color"
                                }
                        )
                    }
                    // Extra bottom stripe to cover half-offset shear arrow
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, (displayData.size + 1) * rowPx) }
                            .height(rowHeight)
                            .fillMaxWidth()
                            .background(
                                if (displayData.size % 2 == 0) RocketBoyTheme.colors.darkPrimary
                                else RocketBoyTheme.colors.lightPrimary
                            )
                            .semantics { contentDescription = "Bottom shear background stripe" }
                    )

                    // Header for shear column
                    ShearComponent(
                        modifier = Modifier
                            .offset { IntOffset(0, rowPx / 2) }
                            .height(rowHeight)
                            .zIndex(1f)
                            .semantics { contentDescription = "Shear column header" },
                        isHeader = true,
                        design = ShearComponentDesign.Light,
                        directionIconRes = null,
                        magnitudeIconRes = null
                    )

                    // Draw shear components on top of stripes
                    displayData.forEachIndexed { index, shear ->
                        val shearDirection = if (shear.shearDirection < 0) shear.shearDirection + 360
                        else shear.shearDirection
                        ShearComponent(
                            modifier           = Modifier
                                .offset { IntOffset(0, (index + 1) * rowPx + rowPx / 2) }
                                .height(rowHeight)
                                .zIndex(1f)
                                .semantics {
                                    contentDescription =
                                        "Shear component for shear magnitude ${shear.shearMagnitude} m/s and direction ${shearDirection}°"
                                },
                            shearDirection     = shearDirection,
                            shearMagnitude     = shear.shearMagnitude,
                            highlightMagnitude = shear.shearMagnitude == topShearMagnitude,
                            hasWarning         = shear.shearMagnitude >= settings.shearMax,
                            design             = if ((index + 1) % 2 == 0) ShearComponentDesign.Light
                            else ShearComponentDesign.Dark,
                            directionIconRes = R.drawable.arrow_icon,
                            magnitudeIconRes = R.drawable.alert_icon
                        )
                    }
                }
            }
        }
    }
}

fun roundToNearestHundred(value: Int) = ((value + 50) / 100) * 100