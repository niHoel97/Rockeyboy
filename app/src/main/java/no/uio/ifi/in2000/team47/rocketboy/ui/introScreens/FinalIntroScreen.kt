package no.uio.ifi.in2000.team47.rocketboy.ui.introScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.ui.animation.RocketLaunchAnimation
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme

@Composable
fun FinalIntroScreen(
    onFinish: () -> Unit
) {
    var launchRocket by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Final intro screen background" }
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
                .padding(horizontal = 24.dp)
                .semantics { contentDescription = "Center text and button" },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ready for Launch",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = RocketBoyTheme.colors.onBackground[1],
                modifier = Modifier.padding(bottom = 16.dp).semantics { contentDescription = "Title text: Ready for Launch" }
            )

            Text(
                text = "You're all set to go!",
                fontSize = 16.sp,
                color = RocketBoyTheme.colors.onBackground[1].copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 24.dp).semantics { contentDescription = "Subtitle text: You're all set to go!" }
            )
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    launchRocket = true
                    delay(1000)
                    launchRocket = false
                    onFinish()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.7f)
                .padding(bottom = 45.dp)
                .semantics { contentDescription = "Start RocketBoy button" },
            colors = ButtonDefaults.buttonColors(
                containerColor = RocketBoyTheme.colors.onBackground[1],
                contentColor = RocketBoyTheme.colors.background[0]
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Start RocketBoy")
        }

        RocketLaunchAnimation(launchRocket)
    }
}

