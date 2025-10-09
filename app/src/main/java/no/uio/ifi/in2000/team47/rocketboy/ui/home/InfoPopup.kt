package no.uio.ifi.in2000.team47.rocketboy.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme

@Composable
fun InfoPopup(
    title: String,
    content: String
) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        Icon(
            painter = painterResource(id = R.drawable.question),
            contentDescription = "Information popup",
            tint = RocketBoyTheme.colors.background[0],
            modifier = Modifier.size(20.dp)
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = RocketBoyTheme.colors.darkSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .semantics {
                        contentDescription = "Information dialog"
                    }
                    .shadow(shape = RoundedCornerShape(16.dp), elevation = 6.dp)
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge.copy(color = RocketBoyTheme.colors.onDarkSecondary),
                                modifier = Modifier.weight(1f).semantics {
                                    contentDescription = "Popup title"
                                }
                            )
                            IconButton(
                                onClick = { showDialog = false },
                                modifier = Modifier.size(28.dp)
                                    .semantics { contentDescription = "Close popup" }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = RocketBoyTheme.colors.darkPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val lines = content.lines()
                        lines.forEach { line ->
                            if (line.trimStart().startsWith("-") || line.trimStart().startsWith("•")) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        color = RocketBoyTheme.colors.onDarkSecondary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                    Text(
                                        text = line.trimStart().removePrefix("-").removePrefix("•").trimStart(),
                                        color = RocketBoyTheme.colors.onDarkSecondary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = 20.sp
                                    )
                                }
                            } else {
                                Text(
                                    text = line,
                                    color = RocketBoyTheme.colors.onDarkSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 6.dp),
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
