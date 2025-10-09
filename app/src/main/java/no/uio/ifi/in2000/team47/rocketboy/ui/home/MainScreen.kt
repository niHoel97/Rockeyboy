package no.uio.ifi.in2000.team47.rocketboy.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.data.grib.GribTab
import no.uio.ifi.in2000.team47.rocketboy.data.settings.SettingsTab
import no.uio.ifi.in2000.team47.rocketboy.ui.calendar.CalendarViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.grib.WindShearScreen
import no.uio.ifi.in2000.team47.rocketboy.ui.settings.SettingsScreen
import no.uio.ifi.in2000.team47.rocketboy.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherScreen
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel

@Composable
fun MainScreen(viewModel: WeatherViewModel, calendarViewModel: CalendarViewModel, settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var selectedSettingsTab by rememberSaveable { mutableStateOf(SettingsTab.LOCATION) }
    var selectedGribTab by rememberSaveable { mutableStateOf(GribTab.TABLE) }

    val coordinates by viewModel.coordinates.collectAsState()
    val lat = coordinates.lat
    val lon = coordinates.lon
    val alt = coordinates.alt

    LaunchedEffect(Unit) {
        viewModel.fetchWeatherAndGribData(lat, lon, alt)
    }

    val (infoTitle, infoContent) = when (currentRoute) {
        "weather" -> "Weather forecast" to """
        - Select a date using the calendar.
        - View available launch windows.
        - Use filters to match your preferences.
        - Tap a window to see detailed data.
    """.trimIndent()

        "windShear" -> when (selectedGribTab) {
            GribTab.TABLE -> "Wind table" to """
            - Forecast for the next 3 hours.
            - Swipe to view more columns.
            - Scroll to see all altitude levels.
        """.trimIndent()

            GribTab.ANALYSIS -> "Wind analysis" to "A compact overview of upper-level wind data.".trimIndent()
            GribTab.LAUNCH -> "Rocket launch" to "Press the Launch button to see the trajectory for your rocket.\n" +
                    "This is just a simplified simulated launch to see the direction and trajectory."
        }
        "settings" -> when (selectedSettingsTab) {
            SettingsTab.LOCATION -> "Location" to """
            Accepted formats:
            - Google Maps: 59°56'35.7"N 10°43'04.3"E
            - Space-separated: <lat> <lon> (<alt>)
            - Comma-separated: <lat>, <lon>, (<alt>)
        """.trimIndent()
            SettingsTab.WEATHER -> "Weather" to """
            - Adjust weather requirements.
            - Press 'Reset' to restore to defaults.
        """.trimIndent()
        }

        else -> "RocketBoy Inc." to "Info"
    }


    Scaffold(
        topBar = {
            LogoHeader(
                infoTitle = infoTitle,
                infoContent = infoContent
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "weather",
            modifier = Modifier.padding(innerPadding)
                .semantics { contentDescription = "Main screen navigation host" }
        ) {
            composable("weather") { WeatherScreen(viewModel, calendarViewModel, settingsViewModel) }
            composable("windShear") { WindShearScreen(viewModel, settingsViewModel, selectedGribTab, { selectedGribTab = it }) }
            composable("settings") { SettingsScreen(viewModel, settingsViewModel, selectedSettingsTab, { selectedSettingsTab = it }) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("weather", "Weather", R.drawable.weatherdrawable),
        BottomNavItem("windShear", "Wind", R.drawable.airdrawable),
        BottomNavItem("settings", "Settings", R.drawable.settingsdrawable)
    )

    NavigationBar(
        containerColor = RocketBoyTheme.colors.onBackground[1],
        modifier = Modifier.semantics { contentDescription = "Bottom navigation bar" }
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(item.icon),
                        contentDescription = "${item.title} icon"
                    )
                },
                label = {
                    Text(item.title, modifier = Modifier.semantics { contentDescription = "${item.title} label" })
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RocketBoyTheme.colors.background[0],
                    selectedTextColor = RocketBoyTheme.colors.background[0],
                    unselectedIconColor = RocketBoyTheme.colors.background[0].copy(alpha = 0.5f),
                    unselectedTextColor = RocketBoyTheme.colors.background[0].copy(alpha = 0.5f),
                    indicatorColor = lerp(RocketBoyTheme.colors.onBackground[0], RocketBoyTheme.colors.onBackground[1], 0.7f)
                ),
                modifier = Modifier.semantics { contentDescription = "Navigate to ${item.title}" }
            )
        }
    }
}
