package no.uio.ifi.in2000.team47.rocketboy.ui.grib

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribTab
import no.uio.ifi.in2000.team47.rocketboy.model.grib.getGribData
import no.uio.ifi.in2000.team47.rocketboy.ui.components.SingleChoiceSegmentedButton
import no.uio.ifi.in2000.team47.rocketboy.ui.grib.wind_share.ShearTable
import no.uio.ifi.in2000.team47.rocketboy.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel

@Composable
fun WindShearScreen(viewModel: WeatherViewModel, settingsViewModel: SettingsViewModel, selectedTab: GribTab, onTabChange: (GribTab)-> Unit) {
    val weatherState by viewModel.weatherState.collectAsState()
    val gribResponseState by viewModel.gribResponseState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val instantAirPressure by viewModel.instantAirPressure.collectAsState()
    val weatherSettings by settingsViewModel.weatherSettings.collectAsState()

    // State for toggling between wind shear table and analysis
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    // State of scrolling page
    val scrollState   = rememberScrollState()
    // State for shear table column fraction
    var shearFraction by rememberSaveable { mutableFloatStateOf(0.7f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(RocketBoyTheme.colors.background))
            .semantics { contentDescription = "Wind Shear Screen" }
    ) {
        SingleChoiceSegmentedButton(
            modifier = Modifier
                .semantics { contentDescription = "Tab selection for Table or Analysis" }
                .zIndex(1f)
                .align(Alignment.TopCenter),
            selectedIndex = selectedIndex,
            options = listOf("Table", "Analysis", "Launch"),
            onSelectChange = { newIndex ->
                selectedIndex = newIndex
                onTabChange(GribTab.entries[newIndex])
            }

        )
        if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier
                        .padding(16.dp)
                        .semantics { contentDescription = "Error message: $errorMessage" }
                )
            }
        } else {
            Column(modifier = if (selectedIndex != 2)Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 70.dp)
                else Modifier
                .fillMaxSize()
            ) {
                when (selectedIndex) {
                    0 -> {

                            weatherSettings?.let {
                                ShearTable(
                                    Modifier,
                                    getGribData(gribResponseState, instantAirPressure),
                                    it
                                )
                            }

                    }
                    1 -> {

                            weatherSettings?.let {
                                ShearAnalysis(
                                    Modifier,
                                    getGribData(gribResponseState, instantAirPressure),
                                    it
                                )
                            }

                    }
                    2 -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .semantics { contentDescription = "Launch Map Screen" }
                        ) {
                            weatherSettings?.let {
                                LaunchMap(weatherViewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

}
