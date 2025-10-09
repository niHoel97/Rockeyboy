package no.uio.ifi.in2000.team47.rocketboy.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team47.rocketboy.model.settings.DoubleRangeSaver
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme


@Composable
fun RangeSliderSetting(
    title: String,
    initialRange: ClosedFloatingPointRange<Double>,
    valueRange: ClosedFloatingPointRange<Double>,
    onValueChange: (ClosedFloatingPointRange<Double>) -> Unit,
    onValueChangeFinished: () -> Unit = {}
) {
    var currentRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(initialRange) }

    LaunchedEffect(initialRange) {
        currentRange = initialRange
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = RocketBoyTheme.colors.onBackground[1]
        )

        RangeSlider(
            value = currentRange.start.toFloat()..currentRange.endInclusive.toFloat(),
            onValueChange = {
                currentRange = it.start.toDouble()..it.endInclusive.toDouble()
                onValueChange(currentRange)
            },
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange.start.toFloat()..valueRange.endInclusive.toFloat(),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .semantics {
                    contentDescription = "Range slider for $title, current range is from ${"%.2f".format(currentRange.start)} to ${"%.2f".format(currentRange.endInclusive)}"
                },
            colors = SliderDefaults.colors(
                thumbColor = RocketBoyTheme.colors.onBackground[1],
                activeTrackColor = RocketBoyTheme.colors.onBackground[1],
                inactiveTrackColor = RocketBoyTheme.colors.onBackground[1].copy(alpha = 0.3f)
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Current min value is ${"%.2f".format(currentRange.start)} and max value is ${"%.2f".format(currentRange.endInclusive)}"
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Min: ${"%.2f".format(currentRange.start)}", fontSize = 14.sp, color = RocketBoyTheme.colors.onBackground[1])
            Text("Max: ${"%.2f".format(currentRange.endInclusive)}", fontSize = 14.sp, color = RocketBoyTheme.colors.onBackground[1])
        }
    }
}
