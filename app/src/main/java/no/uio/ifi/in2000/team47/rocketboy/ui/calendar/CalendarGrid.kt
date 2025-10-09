package no.uio.ifi.in2000.team47.rocketboy.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import java.time.LocalDate

@Composable
fun CalendarGrid(
    month: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    datesWithData: Set<LocalDate>
) {
    val today = LocalDate.now()
    val firstDay = month.withDayOfMonth(1)
    val offset = (firstDay.dayOfWeek.value + 6) % 7
    val daysInMonth = month.lengthOfMonth()
    val cells = List(42) { idx ->
        val day = idx - offset + 1
        if (day in 1..daysInMonth) month.withDayOfMonth(day) else null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { wd ->
                Text(
                    text = wd,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        for (week in 0 until 6) {
            Row(Modifier.fillMaxWidth()) {
                for (dow in 0..6) {
                    val idx = week * 7 + dow
                    val date = cells[idx]
                    val isSelected = date == selectedDate
                    val hasData = date != null && datesWithData.contains(date)
                    val isToday = date == today

                    val borderColor = when {
                        isSelected -> RocketBoyTheme.colors.lightPrimary
                        hasData -> RocketBoyTheme.colors.darkPrimary
                        date != null -> RocketBoyTheme.colors.onBackground[1].copy(alpha = 0.5f)
                        else -> Color.Transparent
                    }
                    val backgroundColor = when {
                        isSelected -> RocketBoyTheme.colors.onBackground[1]
                        hasData -> RocketBoyTheme.colors.onBackground[1].copy(alpha = 0.1f)
                        else -> Color.Transparent
                    }
                    val textColor = when {
                        date == null -> Color.LightGray
                        isSelected -> Color.White
                        else -> Color.Black
                    }

                    // Accessibility content description
                    val description = buildString {
                        if (date != null) {
                            append("Date ${date.dayOfMonth}. ")
                            if (isToday) append("Today. ")
                            if (isSelected) append("Chosen. ")
                            if (hasData) append("Has Data.")
                        } else {
                            append("Empty field.")
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .then(
                                if (date != null) Modifier
                                    .semantics(mergeDescendants = true) {
                                        contentDescription = description
                                        role = Role.Button
                                        onClick(label = "Choose ${date.dayOfMonth}") {
                                            onDateSelected(date)
                                            true
                                        }
                                    }
                                else Modifier
                            )
                            .clickable(enabled = date != null) { date?.let(onDateSelected) }
                            .border(1.dp, borderColor, RoundedCornerShape(6.dp)),
                        color = backgroundColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = date?.dayOfMonth?.toString() ?: "",
                                color = textColor,
                                style = MaterialTheme.typography.bodySmall
                            )

                            if (isToday) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 4.dp)
                                        .size(8.dp)
                                        .background(
                                            color = lerp(RocketBoyTheme.colors.onBackground[0], RocketBoyTheme.colors.onBackground[1], 0.5f),
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
