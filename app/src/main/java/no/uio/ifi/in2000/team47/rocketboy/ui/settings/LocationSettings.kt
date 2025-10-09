package no.uio.ifi.in2000.team47.rocketboy.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel

@Composable
fun LocationSettings(
    weatherViewModel: WeatherViewModel,
    snackbarHostState: SnackbarHostState,
    onLaunch: () -> Unit
) {
    var coordinateInput by rememberSaveable { mutableStateOf("") }
    var coordinateError by rememberSaveable { mutableStateOf<String?>(null) }
    val forskningsparkenDefault = "59°56'51.9\"N 10°43'10.6\"E, 50"
    var successMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Coordinates settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp),
            color = RocketBoyTheme.colors.onBackground[1]
        )

        CoordinateInputField(
            coordinateInput = coordinateInput,
            onValueChange = { coordinateInput = it },
            onReset = {
                coordinateInput = ""
                coordinateError = null
            },
            onDefault = {
                coordinateInput = forskningsparkenDefault
                coordinateError = null
            },
            weatherViewModel = weatherViewModel,
            snackbarHostState = snackbarHostState,
            onLaunch = {
                onLaunch()
                coroutineScope.launch {
                    delay(1000)
                    // launchRocket reset is handled in SettingsScreen via its state
                }
            }
        )
    }
}

