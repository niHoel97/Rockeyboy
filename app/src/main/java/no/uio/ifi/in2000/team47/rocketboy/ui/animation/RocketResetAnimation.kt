package no.uio.ifi.in2000.team47.rocketboy.ui.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.team47.rocketboy.R

@Composable
fun RocketResetAnimation(launchRocket: Boolean) {
    if (!launchRocket) return

    val infiniteTransition = rememberInfiniteTransition(label = "RocketTransition")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -800f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetY"
    )

    LaunchedEffect(launchRocket) {
        delay(1000)
    }

    // Plasser animasjonen i en Box for å unngå påvirkning på layouten
    Box(
        modifier = Modifier
            .fillMaxSize()
            .absoluteOffset(y = offsetY.dp) // Behold animasjonen, men isoler den
    ) {
        Image(
            painter = painterResource(id = R.drawable.rocket), // Ikke bruk .png
            contentDescription = "Rocket",
            modifier = Modifier.align(Alignment.BottomCenter).graphicsLayer(rotationZ = 135f) // Plasser raketten nederst på skjermen,
        )
    }
}