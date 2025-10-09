package no.uio.ifi.in2000.team47.rocketboy.ui.grib

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import com.mapbox.maps.plugin.viewport.ViewportStatus
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.data.trajectory.TrajectoryRepository
import no.uio.ifi.in2000.team47.rocketboy.model.grib.getGribData
import no.uio.ifi.in2000.team47.rocketboy.ui.map.RocketLaunchViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.map.RocketLaunchViewModelFactory
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel
import no.uio.ifi.in2000.team47.rocketboy.utils.Constants


/**
 * Map screen with rocket launch simulation capabilities.
 */
@Composable
fun LaunchMap(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val gribDataList by weatherViewModel.gribResponseState.collectAsState()
    val rocketLaunchViewModel: RocketLaunchViewModel = viewModel(
        factory = RocketLaunchViewModelFactory(
            trajectoryRepository = TrajectoryRepository(),
            gribDataList = getGribData(gribDataList, 1020.8)
        )
    )

    val coordinates by weatherViewModel.coordinates.collectAsState()

    MapboxOptions.accessToken = "pk.eyJ1Ijoibmlsc21lZHNraWxzIiwiYSI6ImNtOGZzNnJvcTBoaHcya3F0bDd0YjJnMG0ifQ.H-8NtN4oeskVT8DJxj0nZg"

    val defaultMapCenter = Point.fromLngLat(coordinates.lon, coordinates.lat)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val rocketPosition by rocketLaunchViewModel.rocketPosition.collectAsState()
    val trajectoryPoints by rocketLaunchViewModel.trajectoryPoints.collectAsState()
    val cameraTarget by rocketLaunchViewModel.cameraTarget.collectAsState()
    val isLaunching by rocketLaunchViewModel.isLaunching.collectAsState()

    val rocketIcon = rememberIconImage(
        key = Constants.ROCKET_ICON_ID,
        painter = painterResource(id = R.drawable.rocket)
    )

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(12.0)
            center(defaultMapCenter)
            pitch(0.0)
        }
    }

    Box(modifier = modifier){
        LaunchedEffect(cameraTarget) {
            cameraTarget?.let { options ->
                val minZoom = 12.0
                val currentZoom = options.zoom ?: minZoom

                val adjustedZoom = if (currentZoom < minZoom) minZoom else currentZoom

                val adjustedOptions = options.toBuilder()
                    .zoom(adjustedZoom)
                    .build()

                mapViewportState.flyTo(
                    cameraOptions = adjustedOptions,
                    animationOptions = MapAnimationOptions.mapAnimationOptions { duration(1000) }
                )
            }
        }

        LaunchedEffect(coordinates) {
            mapViewportState.flyTo(
                cameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(coordinates.lon, coordinates.lat))
                    .zoom(12.0)
                    .build(),
                animationOptions = MapAnimationOptions.mapAnimationOptions { duration(1000) }
            )
        }

        LaunchedEffect(coordinates) {
            rocketLaunchViewModel.setRocketStartPosition(
                Point.fromLngLat(coordinates.lon, coordinates.lat)
            )
        }


        LaunchedEffect(Unit) {
            rocketLaunchViewModel.cameraTarget.collect { initialCameraOpts ->
                if (initialCameraOpts != null && !isLaunching && mapViewportState.mapViewportStatus == ViewportStatus.Idle) {
                    mapViewportState.flyTo(initialCameraOpts, MapAnimationOptions.mapAnimationOptions { duration(500) })
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
                scaleBar = { ScaleBar(
                    modifier = Modifier.absolutePadding(top = 70.dp)
                ) },
                compass = {
                    Compass(
                        modifier = Modifier.absolutePadding(top = 70.dp)
                    )
                },
                logo = {},
                attribution = {}
            ) {
                MapEffect { mapView ->
                    mapView.mapboxMap.removeOnMapClickListener { false }
                    mapView.mapboxMap.apply {
                        loadStyle(
                            style(Style.STANDARD_SATELLITE) {

                            }
                        )
                    }
                }
                if (trajectoryPoints.isNotEmpty()) {
                    PolylineAnnotation(
                        points = trajectoryPoints,
                    ) {
                        lineWidth = 2.5
                    }
                }

                rocketPosition?.let { pos ->
                    PointAnnotation(
                        point = pos,
                    ) {
                        iconImage = rocketIcon
                        iconSize = 0.2
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .semantics { contentDescription = "Confirmation message" },
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

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Button(
                        onClick = {
                            val launchPoint = rocketLaunchViewModel.rocketPosition.value
                            rocketLaunchViewModel.startLaunchSequence(launchPoint)
                        },
                        enabled = !isLaunching,
                        modifier = Modifier
                            .weight(1f)
                            .semantics { contentDescription = "Launch Rocket Button" },
                        colors = ButtonDefaults.buttonColors(RocketBoyTheme.colors.darkPrimary)
                    ) {
                        if (isLaunching) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Launching...")
                        } else {
                            Text("Launch Rocket")
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Button(
                        onClick = { rocketLaunchViewModel.resetSimulation(defaultMapCenter) },
                        enabled = isLaunching || trajectoryPoints.isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .semantics { contentDescription = "Reset Simulation Button" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RocketBoyTheme.colors.darkSecondary,
                            contentColor = RocketBoyTheme.colors.onDarkSecondary,
                            disabledContainerColor = RocketBoyTheme.colors.darkSecondary.copy(alpha = 0.5f),
                            disabledContentColor = RocketBoyTheme.colors.onDarkSecondary.copy(alpha = 0.5f)
                        )
                    ) {
                        Text("Reset")
                    }
                }
            }
        }
    }
}
