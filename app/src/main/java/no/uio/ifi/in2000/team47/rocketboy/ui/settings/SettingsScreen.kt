package no.uio.ifi.in2000.team47.rocketboy.ui.settings

import DismissKeyboardOnTap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.team47.rocketboy.data.settings.SettingsTab
import no.uio.ifi.in2000.team47.rocketboy.ui.animation.RocketLaunchAnimation
import no.uio.ifi.in2000.team47.rocketboy.ui.components.SingleChoiceSegmentedButton
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel


@Composable
fun SettingsScreen(
    viewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel,
    selectedTab: SettingsTab,
    onTabChange: (SettingsTab) -> Unit
) {
    val weatherSettings by settingsViewModel.weatherSettings.collectAsState()
    var selectedIndex by rememberSaveable { mutableIntStateOf(selectedTab.ordinal) }
    var launchRocket by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    // State of scrolling page
    val scrollState   = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(RocketBoyTheme.colors.background))
            .semantics { contentDescription = "Settings Screen" }
    ) {
        SingleChoiceSegmentedButton(
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopCenter)
                .semantics { contentDescription = "Tab selection for location or weather settings" },
            selectedIndex = selectedIndex,
            onSelectChange = { newIndex ->
                selectedIndex = newIndex
                onTabChange(SettingsTab.entries[newIndex]) },
            options = listOf("Location", "Weather")
        )

        DismissKeyboardOnTap {
            LaunchedEffect(launchRocket) {
                if (launchRocket) {
                    delay(1000)
                    launchRocket = false
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(70.dp))
                when (selectedIndex) {
                    0 -> {
                        LocationSettings(
                            weatherViewModel = viewModel,
                            snackbarHostState = snackbarHostState,
                            onLaunch = { launchRocket = true }
                        )
                    }
                    1 -> {
                        WeatherSettings(
                            weatherSettings = weatherSettings,
                            settingsViewModel = settingsViewModel,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }

            RocketLaunchAnimation(launchRocket)

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp).semantics { contentDescription = "Error/Confirmation message" },
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
