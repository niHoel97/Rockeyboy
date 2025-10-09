package no.uio.ifi.in2000.team47.rocketboy.ui.grib.wind_share

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme

enum class ShearComponentDesign { Dark, Light }

@Composable
fun ShearComponent(
    modifier: Modifier = Modifier,
    design: ShearComponentDesign = ShearComponentDesign.Dark,
    isHeader: Boolean? = false,
    highlightMagnitude: Boolean? = false,
    hasWarning: Boolean? = false,
    shearDirection: Double? = null,
    shearMagnitude: Double? = null,
    onLongPressedSplit: () -> Unit = {},
    onTappedSplit: () -> Unit = {},
    onDoubleTappedSplit: () -> Unit = {},
    @DrawableRes directionIconRes: Int? = null,
    @DrawableRes magnitudeIconRes: Int? = null,
) {
    val shearSplit = R.drawable.shear_split_gray
    val backgroundColor = if (design == ShearComponentDesign.Dark) RocketBoyTheme.colors.darkSecondary else RocketBoyTheme.colors.lightSecondary

    var size = remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    // funnel width
    val funnelDp = 67.dp
    val funnelPx = with(density) { funnelDp.roundToPx() }

    // show content at given widths
    val showDirection = remember(size.value) {
        with(density) { size.value.width.toDp() > 190.dp }
    }
    val showWarning = remember(size.value) {
        with(density) { size.value.width.toDp() > 207.dp }
    }

    // See comment in Layout scope for reasoning behind it's use here.
    Layout(
        modifier = modifier
            .onSizeChanged { size.value = it }
            .height(52.dp)
            .clipToBounds(),
        content = {
            // 1) funnel SVG
            Box(
                Modifier
                    .size(width = funnelDp, height = 52.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onTappedSplit() },
                            onDoubleTap = { onDoubleTappedSplit() },
                            onLongPress = { onLongPressedSplit() }
                        )
                    }
            ) {
                Icon(
                    painter = painterResource(shearSplit),
                    contentDescription = "Shear Split",
                    tint = if (design == ShearComponentDesign.Dark) RocketBoyTheme.colors.darkSecondary else RocketBoyTheme.colors.lightSecondary
                )
            }
            // 2) body content
            Row(
                Modifier
                    .background(backgroundColor)
                    .fillMaxHeight()
                    .padding(vertical = 0.dp)
                    .padding(end = 100.dp)
                    .semantics {
                        contentDescription = "Body content for wind shear component."
                    },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Top title row
                    if (isHeader == true) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .semantics {
                                    contentDescription = "Header row showing Wind Shear title."
                                },
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Wind Shear",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                    // Second row: column headings or values
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Direction column
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isHeader == true) {
                                Text(
                                    text = "Direction",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = RocketBoyTheme.colors.darkPrimary,
                                    maxLines = 1
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                    if (directionIconRes != null) Box(
                                        Modifier
                                            .size(width = 9.dp, height = 19.dp)
                                            .semantics {
                                                contentDescription = "Wind shear direction icon"
                                            }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = directionIconRes),
                                            contentDescription = shearDirection?.let { "Wind shear direction icon rotated at ${it.toInt()}°" }
                                                ?: "Wind shear direction icon",
                                            modifier = Modifier.rotate((shearDirection ?: 0.0).toFloat()),
                                            tint = RocketBoyTheme.colors.background[1]
                                        )
                                    }
                                    if (showDirection) {
                                        Text(
                                            text = shearDirection?.let { "${it.toInt()}°" } ?: "?",
                                            fontSize = 13.sp,
                                            textAlign = TextAlign.Left,
                                            lineHeight = 16.sp,
                                            modifier = Modifier.height(16.38.dp).semantics {
                                                contentDescription = "Direction of wind shear: ${shearDirection?.toInt() ?: "unknown"} degrees."
                                            },
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                        // Magnitude column
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isHeader == true) {
                                Text(
                                    text = "Magnitude",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = RocketBoyTheme.colors.darkPrimary,
                                    maxLines = 1
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = shearMagnitude?.let { "$it m/s" } ?: "?",
                                        fontSize = 13.sp,
                                        fontWeight = if (highlightMagnitude == true) FontWeight.ExtraBold else FontWeight.Normal,
                                        textAlign = TextAlign.Left,
                                        modifier = Modifier.width(67.dp).semantics{
                                            contentDescription = "Wind shear magnitude: ${shearMagnitude?.toInt() ?: "unknown"} meters per second."
                                        },
                                        maxLines = 1
                                    )
                                    if (showWarning && hasWarning == true && magnitudeIconRes != null) Box(Modifier.size(21.dp)) {
                                        Image(
                                            painter = painterResource(id = magnitudeIconRes),
                                            contentDescription = "Warning icon indicating high shear magnitude"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { measurables, constraints ->
        /**
         * For outside content (isobar data) to clip between the funnels
         * of the shear-components, we need to manually alter the content
         * space.
         */
        // pretend we are wider by funnelPx
        val targetWidth = constraints.maxWidth + funnelPx
        val expanded = constraints.copy(minWidth = targetWidth, maxWidth = targetWidth)
        // measure both children under that extra width
        val placeables = measurables.map { it.measure(expanded) }
        // all children have same height
        val height = placeables.first().height
        layout(targetWidth, height) {
            // place funnel at left
            placeables[0].place(0, 0)
            // place body shifted right by funnelPx
            placeables[1].place(funnelPx, 0)
        }
    }
}