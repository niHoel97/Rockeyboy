package no.uio.ifi.in2000.team47.rocketboy.ui.introScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme

@Composable
fun FirstIntroScreen(
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "First intro screen background" }
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
                text = "Welcome to RocketBoy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.semantics { contentDescription = "Title text: Welcome to RocketBoy" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your rocket-launch planning assistant",
                fontSize = 16.sp,
                color = Color.DarkGray,
                modifier = Modifier.semantics { contentDescription = "Subtitle text: Your rocket-launch planning assistant" }
            )
        }

        Button(
            onClick = onNext,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 45.dp)
                .fillMaxWidth(0.7f)
                .semantics { contentDescription = "Get Started button" },
            colors = ButtonDefaults.buttonColors(
                containerColor = RocketBoyTheme.colors.onBackground[1],
                contentColor = RocketBoyTheme.colors.background[0]
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Get Started")
        }
    }
}
