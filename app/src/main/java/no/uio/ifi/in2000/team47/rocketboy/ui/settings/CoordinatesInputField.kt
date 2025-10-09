package no.uio.ifi.in2000.team47.rocketboy.ui.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.model.settings.parseCoordinatesInput
import no.uio.ifi.in2000.team47.rocketboy.model.settings.submitChanges
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel

@Composable
fun CoordinateInputField(
    coordinateInput: String,
    onValueChange: (String) -> Unit,
    onReset: () -> Unit,
    onDefault: () -> Unit,
    weatherViewModel: WeatherViewModel,
    snackbarHostState: SnackbarHostState,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var coordinateError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        // Input field
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = modifier) {
                Column(
                    modifier = Modifier
                        .width(400.dp)
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(2.dp)),
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = coordinateInput,
                        onValueChange = onValueChange,
                        label = { Text("Lat, lon, (alt)") },
                        isError = coordinateError != null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                            .semantics {
                                contentDescription = "Enter coordinates in the format: Latitude, Longitude, (Altitude)"
                            },
                        trailingIcon = {
                            IconButton(onClick = onReset) {
                                Icon(
                                    painter = painterResource(id = R.drawable.trash),
                                    contentDescription = "Reset coordinates",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.LightGray,
                            errorContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Submit button
            Button(
                onClick = {
                    try {
                        val coordinates = parseCoordinatesInput(coordinateInput)
                        val error = submitChanges(
                            coordinates.lat,
                            coordinates.lon,
                            coordinates.alt,
                            weatherViewModel,
                            onLatError = { coordinateError = it },
                            onLonError = { coordinateError = it },
                            onAltError = { coordinateError = it }
                        )

                        if (error == null) {
                            coordinateError = null
                            successMessage = "Coordinates updated."
                            onLaunch()

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = successMessage ?: "Updated.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } else {
                            coordinateError = error.toString()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = error.toString(),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    } catch (e: Exception) {
                        coordinateError = "Invalid input."
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = coordinateError ?: "Invalid input.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RocketBoyTheme.colors.onBackground[1],
                    contentColor = RocketBoyTheme.colors.background[0]
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Submit")
            }

            // Default button
            Button(
                onClick = {
                    onDefault()
                    coordinateError = null
                    successMessage = null
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RocketBoyTheme.colors.onBackground[1],
                    contentColor = RocketBoyTheme.colors.background[0]
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Default")
            }
        }
    }
}
