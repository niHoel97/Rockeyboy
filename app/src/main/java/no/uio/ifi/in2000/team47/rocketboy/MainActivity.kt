package no.uio.ifi.in2000.team47.rocketboy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import no.uio.ifi.in2000.team47.rocketboy.data.db.DatabaseProvider
import no.uio.ifi.in2000.team47.rocketboy.data.db.repository.WeatherSettingsRepository
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribDataSource
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribRepository
import no.uio.ifi.in2000.team47.rocketboy.data.locationforecast.LocationforecastDataSource
import no.uio.ifi.in2000.team47.rocketboy.data.locationforecast.LocationforecastRepository
import no.uio.ifi.in2000.team47.rocketboy.data.network.HttpClientProvider
import no.uio.ifi.in2000.team47.rocketboy.ui.calendar.CalendarViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.calendar.CalendarViewModelFactory
import no.uio.ifi.in2000.team47.rocketboy.ui.home.MainScreen
import no.uio.ifi.in2000.team47.rocketboy.ui.introScreens.IntroScreens
import no.uio.ifi.in2000.team47.rocketboy.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.settings.SettingsViewModelFactory
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val httpClient = HttpClientProvider.client
        val locationforecastDataSource = LocationforecastDataSource(httpClient)
        val gribDataSource = GribDataSource(httpClient)
        val locationforecastRepository = LocationforecastRepository(locationforecastDataSource)
        val gribRepository = GribRepository(gribDataSource)
        val factory = WeatherViewModelFactory(locationforecastRepository, gribRepository)
        val viewModel: WeatherViewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)
        val calendarFactory = CalendarViewModelFactory(locationforecastRepository)
        val calendarViewModel: CalendarViewModel = ViewModelProvider(this, calendarFactory).get(CalendarViewModel::class.java)

        val database = DatabaseProvider.getDatabase(this)
        val weatherSettingsRepository = WeatherSettingsRepository(database.weatherSettingsDao())
        val settingsViewModelFactory = SettingsViewModelFactory(weatherSettingsRepository)
        val settingsViewModel: SettingsViewModel = ViewModelProvider(this, settingsViewModelFactory).get(SettingsViewModel::class.java)

        val sharedPref = getSharedPreferences("rocketboy_prefs", MODE_PRIVATE)


        setContent {
            val isFirstLaunch = remember {
                mutableStateOf(sharedPref.getBoolean("is_first_launch", true))
            }
            RocketBoyTheme() {
                if (isFirstLaunch.value) {
                    IntroScreens(
                        onFinished = {
                            sharedPref.edit() { putBoolean("is_first_launch", false) }
                            isFirstLaunch.value = false
                        },
                        viewModel = viewModel,
                        settingsViewModel
                    )
                } else {
                    MainScreen(
                        viewModel = viewModel,
                        calendarViewModel = calendarViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
