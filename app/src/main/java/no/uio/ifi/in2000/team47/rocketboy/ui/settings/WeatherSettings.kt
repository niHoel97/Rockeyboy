package no.uio.ifi.in2000.team47.rocketboy.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.data.db.entity.WeatherSettingsData
import no.uio.ifi.in2000.team47.rocketboy.model.settings.DoubleRangeSaver
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme

@Composable
fun WeatherSettings(
    weatherSettings: WeatherSettingsData?,
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState
) {
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
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(weatherSettings) {
        weatherSettings?.let { settings ->
            tempRange = settings.tempMin..settings.tempMax
            humidityRange = settings.humidityMin..settings.humidityMax
            windRange = settings.windMin..settings.windMax
            precipitationRange = settings.precipitationMin..settings.precipitationMax
            fogRange = settings.fogMin..settings.fogMax
            dewRange = settings.dewMin..settings.dewMax
            cloudLowRange = settings.cloudLowMin..settings.cloudLowMax
            cloudMediumRange = settings.cloudMediumMin..settings.cloudMediumMax
            cloudHighRange = settings.cloudHighMin..settings.cloudHighMax
            shearWindRange = settings.shearMin..settings.shearMax
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Weather settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = RocketBoyTheme.colors.onBackground[1]
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    tempRange = 0.0..35.0
                    humidityRange = 0.0..75.0
                    windRange = 0.0..8.5
                    precipitationRange = 0.0..0.0
                    fogRange = 0.0..0.0
                    dewRange = -3.0..15.0
                    cloudLowRange = 0.0..5.0
                    cloudMediumRange = 0.0..15.0
                    cloudHighRange = 0.0..15.0
                    shearWindRange = 0.0..24.5
                    settingsViewModel.resetWeatherSettingsToDefault()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Settings reset")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Reset Button" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = RocketBoyTheme.colors.onBackground[1],
                    contentColor = RocketBoyTheme.colors.background[0]
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RangeSliderSetting(
            "Temperature (°C)",
            tempRange,
            -20.0..45.0,
            onValueChange = { tempRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
            }
        )
        RangeSliderSetting(
            "Humidity (%)",
            humidityRange,
            0.0..100.0,
            onValueChange = { humidityRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
            }
        )
        RangeSliderSetting(
            "Windspeed (m/s)",
            windRange,
            0.0..20.0,
            onValueChange = { windRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
                coroutineScope.launch{

                }
            }
        )
        RangeSliderSetting(
            "Precipitation (mm)",
            precipitationRange,
            0.0..15.0,
            onValueChange = { precipitationRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
            }
        )
        RangeSliderSetting(
            "Fog (%)",
            fogRange,
            0.0..100.0,
            onValueChange = { fogRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
            }
        )
        RangeSliderSetting(
            "Dew point (°C)",
            dewRange,
            -10.0..30.0,
            onValueChange = { dewRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
            }
        )
        RangeSliderSetting(
            "Cloud fraction low (%)",
            cloudLowRange,
            0.0..100.0,
            onValueChange = { cloudLowRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
            }
        )
        RangeSliderSetting(
            "Cloud fraction medium (%)",
            cloudMediumRange,
            0.0..100.0,
            onValueChange = { cloudMediumRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
            }
        )
        RangeSliderSetting(
            "Cloud fraction high (%)",
            cloudHighRange,
            0.0..100.0,
            onValueChange = { cloudHighRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
            }
        )
        RangeSliderSetting(
            "Shear wind (m/s)",
            shearWindRange,
            0.0..40.0,
            onValueChange = { shearWindRange = it },
            onValueChangeFinished = {
                settingsViewModel.updateWeatherSettings(
                    WeatherSettingsData(
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
                )
            }
        )
    }
}
