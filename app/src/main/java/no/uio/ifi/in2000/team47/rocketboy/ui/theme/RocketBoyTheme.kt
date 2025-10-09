package no.uio.ifi.in2000.team47.rocketboy.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class RocketBoyColors(
    val background: List<Color>,
    val onBackground: List<Color>,
    val darkPrimary: Color,
    val lightPrimary: Color,
    val onDarkPrimary: Color,
    val onLightPrimary: Color,
    val darkSecondary: Color,
    val lightSecondary: Color,
    val onDarkSecondary: Color,
    val onLightSecondary: Color,
    val red: Color,
    val yellow: Color,
    val green: Color
)

@Immutable
data class RocketBoyTypography(
    val body: TextStyle,
    val title: TextStyle
)

@Immutable
data class RocketBoyElevation(
    val default: Dp,
    val pressed: Dp
)

val LocalBlueLightTheme = staticCompositionLocalOf {
    RocketBoyColors(
        background = listOf(Color.White, Color(0xFF6E92D2)),
        onBackground = listOf(Color.White, Color(0xFF243952)),
        darkPrimary = Color(0xFF264275),
        lightPrimary = Color(0xFF6C9EEA),
        onDarkPrimary = Color(0xFFF4F4F5),
        onLightPrimary = Color(0xFF273A5D),
        darkSecondary = Color(0xFFD5D5D5),
        lightSecondary = Color(0xFFE8E8E8),
        onDarkSecondary = Color(0xFF000000),
        onLightSecondary = Color(0xFF000000),
        red = Color(0xFFB13031),
        yellow = Color(0xFFF4B913),
        green = Color(0xFF4BA84F)
    )
}

val LocalRedLightTheme = staticCompositionLocalOf {
    RocketBoyColors(
        background = listOf(Color.White, Color(0xFFE34F4F)),
        onBackground = listOf(Color.White, Color(0xFF551E1E)),
        darkPrimary = Color(0xFFA11A1A),
        lightPrimary = Color(0xFFF58A8A),
        onDarkPrimary = Color(0xFFF5F4F4),
        onLightPrimary = Color(0xFF5E1818),
        darkSecondary = Color(0xFFD5D5D5),
        lightSecondary = Color(0xFFE8E8E8),
        onDarkSecondary = Color(0xFF000000),
        onLightSecondary = Color(0xFF000000),
        red = Color(0xFFB13031),
        yellow = Color(0xFFF4B913),
        green = Color(0xFF4BA84F)
    )
}

val LocalRocketBoyTypography = staticCompositionLocalOf {
    RocketBoyTypography(
        body = TextStyle.Default,
        title = TextStyle.Default
    )
}
val LocalRocketBoyElevation = staticCompositionLocalOf {
    RocketBoyElevation(
        default = 6.dp,
        pressed = 20.dp
    )
}

val LocalRocketBoyColors = LocalBlueLightTheme

@Composable
fun RocketBoyTheme(
    colors: RocketBoyColors = LocalRocketBoyColors.current,
    typography: RocketBoyTypography = LocalRocketBoyTypography.current,
    elevation: RocketBoyElevation = LocalRocketBoyElevation.current,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalRocketBoyColors provides colors,
        LocalRocketBoyTypography provides typography,
        LocalRocketBoyElevation provides elevation,
        content = content
    )
}

// Usage: RocketBoyTheme.elevation.pressed
object RocketBoyTheme {
    val colors: RocketBoyColors
        @Composable
        get() = LocalRocketBoyColors.current
    val typography: RocketBoyTypography
        @Composable
        get() = LocalRocketBoyTypography.current
    val elevation: RocketBoyElevation
        @Composable
        get() = LocalRocketBoyElevation.current
}
