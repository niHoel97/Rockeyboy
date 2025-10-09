package no.uio.ifi.in2000.team47.rocketboy.ui.introScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team47.rocketboy.ui.home.LogoHeader
import no.uio.ifi.in2000.team47.rocketboy.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import no.uio.ifi.in2000.team47.rocketboy.ui.weather.WeatherViewModel

@Composable
fun IntroScreens(
    onFinished: () -> Unit,
    viewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val (infoTitle, infoContent) = when (currentRoute) {
        "first" -> "Welcome" to "Start by learning what RocketBoy is and what the app can do."
        "second" -> "Coordinate setup" to """
            Accepted formats:
            - Google Maps: 59°56'35.7"N 10°43'04.3"E
            - Space-separated: <lat> <lon> (<alt>)
            - Comma-separated: <lat>, <lon>, (<alt>)
        """.trimIndent()
        "fourth" -> "Weather" to """
            - Adjust weather requirements.
            - Press 'Save and Continue' to store them.
            - Press 'Reset' to restore to defaults.
        """.trimIndent()
        "final" -> "You're all set!" to "You're now ready to launch rockets and explore the skies!"
        else -> "RocketBoy" to "Configure your app before launching into the full experience."
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                RocketBoyTheme.colors.background[0]
            )
    ) {
        Scaffold(
            topBar = {
                LogoHeader(
                    infoTitle = infoTitle.toString(),
                    infoContent = infoContent
                )
            }
        ) { contentPadding -> //Ignore warning, we don't want to use the contentPadding
            NavHost(
                navController = navController,
                startDestination = "first",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("first") {
                    FirstIntroScreen(
                        onNext = { navController.navigate("second") }
                    )
                }
                composable("second") {
                    SecondIntroScreen(
                        onNext = { navController.navigate("fourth") },
                        viewModel = viewModel
                    )
                }
                composable("fourth") {
                    FourthIntroScreen(
                        onNext = { navController.navigate("final") },
                        settingsViewModel = settingsViewModel
                    )
                }
                composable("final") {
                    FinalIntroScreen(
                        onFinish = { onFinished() }
                    )
                }
            }
        }
    }
}

