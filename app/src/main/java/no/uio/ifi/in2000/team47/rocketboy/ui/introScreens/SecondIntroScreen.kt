package no.uio.ifi.in2000.team47.rocketboy.ui.introScreens

import DismissKeyboardOnTap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.model.settings.parseCoordinatesInput
import no.uio.ifi.in2000.team47.rocketboy.model.settings.submitChanges
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel

@Composable
fun SecondIntroScreen(
    onNext: () -> Unit,
    viewModel: WeatherViewModel
) {
    DismissKeyboardOnTap {
        var coordinateInput by remember { mutableStateOf("") }
        var coordinateError by rememberSaveable { mutableStateOf<String?>(null) }

        val snackbarHostState = remember { SnackbarHostState() }
        val forskningsparkenDefault = "59°56'51.9\"N 10°43'10.6\"E, 50"
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = "Second intro screen for setting up the coordinates" }
                .background(Brush.linearGradient(RocketBoyTheme.colors.background))
        ) {
            Image(
                painter = painterResource(id = R.drawable.rocket),
                contentDescription = "Rocket image at top right corner",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-40).dp, y = 100.dp)
                    .graphicsLayer(alpha = 0.08f)
            )

            Image(
                painter = painterResource(id = R.drawable.rocket),
                contentDescription = "Rocket image at bottom left corner",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = 40.dp, y = (-100).dp)
                    .graphicsLayer(alpha = 0.08f, rotationY = 180f)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Plan your rocket launch",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RocketBoyTheme.colors.onBackground[1]
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Insert your launch-coordinates, or use our default values for Forskningsparken.\nYou can change them at any time later",
                    fontSize = 16.sp,
                    color = RocketBoyTheme.colors.onBackground[1],
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, RoundedCornerShape(2.dp)),
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = coordinateInput,
                        onValueChange = {
                            coordinateInput = it
                            coordinateError = null
                        },
                        label = { Text("Lat, lon, (alt)") },
                        isError = coordinateError != null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Enter coordinates in the format: Latitude, Longitude, (Altitude)"
                            },
                        trailingIcon = {
                            IconButton(onClick = {
                                coordinateInput = ""
                                coordinateError = null
                            }) {
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

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        coordinateInput = forskningsparkenDefault
                        coordinateError = null
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RocketBoyTheme.colors.onBackground[1],
                        contentColor = RocketBoyTheme.colors.background[0]
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Default")
                }
            }

            Button(
                onClick = {
                    try {
                        val coordinates = parseCoordinatesInput(coordinateInput)
                        val error = submitChanges(
                            coordinates.lat,
                            coordinates.lon,
                            coordinates.alt,
                            viewModel,
                            onLatError = { coordinateError = it },
                            onLonError = { coordinateError = it },
                            onAltError = { coordinateError = it }
                        )

                        if (error == null) {
                            coordinateError = null
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Coordinates updated.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            onNext()
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
                Text("Submit")
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 45.dp)
                    .fillMaxWidth(0.7f)
                    .semantics { contentDescription = "Error/Confirmation message" },
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = RocketBoyTheme.colors.darkSecondary,
                        contentColor = RocketBoyTheme.colors.onDarkSecondary,
                        shape = MaterialTheme.shapes.medium,
                        actionColor = RocketBoyTheme.colors.onDarkSecondary,
                        actionContentColor = RocketBoyTheme.colors.darkSecondary
                    )
                }
            )
        }
    }
}


















