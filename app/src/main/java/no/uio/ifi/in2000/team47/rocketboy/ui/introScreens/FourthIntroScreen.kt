package no.uio.ifi.in2000.team47.rocketboy.ui.introScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.data.db.DatabaseProvider
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.model.settings.DoubleRangeSaver
import no.uio.ifi.in2000.team47.rocketboy.ui.settings.RangeSliderSetting
import no.uio.ifi.in2000.team47.rocketboy.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme

@Composable
fun FourthIntroScreen(
    onNext: () -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val db = remember { DatabaseProvider.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

    var tempRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(0.0..35.0) }
    var humidityRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(0.0..75.0) }
    var windRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(0.0..8.5) }
    var precipitationRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(0.0..0.0) }
    var fogRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(0.0..0.0) }
    var dewRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(-3.0..15.0) }
    var cloudLowRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(0.0..5.0) }
    var cloudMediumRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(0.0..15.0) }
    var cloudHighRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(0.0..15.0) }
    var shearWindRange by rememberSaveable(stateSaver = DoubleRangeSaver) { mutableStateOf(0.0..24.5) }

    val defaultRanges = mapOf(
        "temp" to 0.0..35.0,
        "humidity" to 0.0..75.0,
        "wind" to 0.0..8.5,
        "precip" to 0.0..0.0,
        "fog" to 0.0..0.0,
        "dew" to -3.0..15.0,
        "cloudLow" to 0.0..8.5,
        "cloudMedium" to 0.0..15.0,
        "cloudHigh" to 0.0..15.0,
        "shear" to 0.0..24.5
    )

    Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(RocketBoyTheme.colors.background))) {
        // Background rockets
        Image(
            painter = painterResource(id = R.drawable.rocket),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-40).dp, y = 100.dp)
                .graphicsLayer(alpha = 0.08f)
        )

        Image(
            painter = painterResource(id = R.drawable.rocket),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomStart)
                .offset(x = 40.dp, y = (-100).dp)
                .graphicsLayer(alpha = 0.08f, rotationY = 180f)
        )

        // Top text
        Text(
            text = "Choose your weather preferences.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 90.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
        )

        // Reset button
        Button(
            onClick = {
                tempRange = defaultRanges["temp"]!!
                humidityRange = defaultRanges["humidity"]!!
                windRange = defaultRanges["wind"]!!
                precipitationRange = defaultRanges["precip"]!!
                fogRange = defaultRanges["fog"]!!
                dewRange = defaultRanges["dew"]!!
                cloudLowRange = defaultRanges["cloudLow"]!!
                cloudMediumRange = defaultRanges["cloudMedium"]!!
                cloudHighRange = defaultRanges["cloudHigh"]!!
                shearWindRange = defaultRanges["shear"]!!
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 140.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .semantics { contentDescription = "Reset button" },
            colors = ButtonDefaults.buttonColors(
                containerColor = RocketBoyTheme.colors.onBackground[1],
                contentColor = RocketBoyTheme.colors.background[0]
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Reset")
        }

        // Save button
        Button(
            onClick = {
                coroutineScope.launch {
                    val settings = WeatherSettingsData(
                        tempMin = tempRange.start,
                        tempMax = tempRange.endInclusive,
                        humidityMin = humidityRange.start,
                        humidityMax = humidityRange.endInclusive,
                        windMin = windRange.start,
                        windMax = windRange.endInclusive,
                        precipitationMin = precipitationRange.start,
                        precipitationMax = precipitationRange.endInclusive,
                        fogMin = fogRange.start,
                        fogMax = fogRange.endInclusive,
                        dewMin = dewRange.start,
                        dewMax = dewRange.endInclusive,
                        cloudLowMin = cloudLowRange.start,
                        cloudLowMax = cloudLowRange.endInclusive,
                        cloudMediumMin = cloudMediumRange.start,
                        cloudMediumMax = cloudMediumRange.endInclusive,
                        cloudHighMin = cloudHighRange.start,
                        cloudHighMax = cloudHighRange.endInclusive,
                        shearMin = shearWindRange.start,
                        shearMax = shearWindRange.endInclusive
                    )
                    db.weatherSettingsDao().insert(settings)
                    settingsViewModel.updateWeatherSettings(settings)
                    onNext()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 45.dp)
                .fillMaxWidth(0.7f)
                .semantics { contentDescription = "Submit button" },
            colors = ButtonDefaults.buttonColors(
                containerColor = RocketBoyTheme.colors.onBackground[1],
                contentColor = RocketBoyTheme.colors.background[0]
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Save and Continue")
        }

        // LazyColumn filling available space
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 220.dp, bottom = 110.dp) // Space between Reset and Submit
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                RangeSliderSetting(
                    title = "Temperature (°C)",
                    initialRange = tempRange,
                    valueRange = -20.0..45.0,
                    onValueChange = { tempRange = it }
                )
            }
            item {
                RangeSliderSetting(
                    title = "Humidity (%)",
                    initialRange = humidityRange,
                    valueRange = 0.0..100.0,
                    onValueChange = { humidityRange = it }
                )
            }
            item {
                RangeSliderSetting(
                    title = "Windspeed (m/s)",
                    initialRange = windRange,
                    valueRange = 0.0..20.0,
                    onValueChange = { windRange = it }
                )
            }
            item {
                RangeSliderSetting(
                    title = "Precipitation (mm)",
                    initialRange = precipitationRange,
                    valueRange = 0.0..15.0,
                    onValueChange = { precipitationRange = it }
                )
            }
            item {
                RangeSliderSetting(
                    title = "Fog (%)",
                    initialRange = fogRange,
                    valueRange = 0.0..100.0,
                    onValueChange = { fogRange = it }
                )
            }
            item {
                RangeSliderSetting(
                    title = "Dew point (°C)",
                    initialRange = dewRange,
                    valueRange = -10.0..30.0,
                    onValueChange = { dewRange = it }
                )
            }
            item {
                RangeSliderSetting(
                    title = "Cloud fraction low (%)",
                    initialRange = cloudLowRange,
                    valueRange = 0.0..100.0,
                    onValueChange = { cloudLowRange = it }
                )
            }
            item {
                RangeSliderSetting(
                    title = "Cloud fraction medium (%)",
                    initialRange = cloudMediumRange,
                    valueRange = 0.0..100.0,
                    onValueChange = { cloudMediumRange = it }
                )
            }
            item {
                RangeSliderSetting(
                    title = "Cloud fraction high (%)",
                    initialRange = cloudHighRange,
                    valueRange = 0.0..100.0,
                    onValueChange = { cloudHighRange = it }
                )
            }
            item {
                RangeSliderSetting(
                    title = "Shear wind (m/s)",
                    initialRange = shearWindRange,
                    valueRange = 0.0..40.0,
                    onValueChange = { shearWindRange = it }
                )
            }
        }
    }

}


