package no.uio.ifi.in2000.team47.rocketboy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    options: List<String>,
    onSelectChange: (Int) -> Unit  // Use callback to notify parent of the selected index
) {
    Card(
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(RocketBoyTheme.colors.darkSecondary.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .padding(10.dp)
            .height(50.dp)
            .semantics { contentDescription = "Single choice segmented button" },
    ) {
        Row(
            modifier = Modifier
                .padding(5.dp)
                .background(Color.Transparent)
        ) {
            options.forEachIndexed { index, label ->
                Card(
                    onClick = { onSelectChange(index) },
                    shape = RoundedCornerShape(5.dp),
                    colors = CardDefaults.cardColors(Color.Transparent),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (selectedIndex == index){
                                    RocketBoyTheme.colors.lightSecondary
                                } else
                                    Color.Transparent
                            )
                    ) {
                        Text(
                            label,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedIndex == index){
                                RocketBoyTheme.colors.onLightSecondary
                            } else
                                RocketBoyTheme.colors.onDarkSecondary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
