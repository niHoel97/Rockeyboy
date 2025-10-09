package no.uio.ifi.in2000.team47.rocketboy.ui.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team47.rocketboy.R
import no.uio.ifi.in2000.team47.rocketboy.data.locationforecast.RatedForecast
import no.uio.ifi.in2000.team47.rocketboy.model.ratings.getRating
import no.uio.ifi.in2000.team47.rocketboy.ui.calendar.CalendarGrid
import no.uio.ifi.in2000.team47.rocketboy.ui.calendar.CalendarViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.calendar.NoConnectionCard
import no.uio.ifi.in2000.team47.rocketboy.ui.calendar.NoDataCard
import no.uio.ifi.in2000.team47.rocketboy.ui.calendar.NoSuitableDataCard
import no.uio.ifi.in2000.team47.rocketboy.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.team47.rocketboy.ui.theme.RocketBoyTheme
import java.time.format.DateTimeFormatter

@Composable
fun WeatherScreen(viewModel: WeatherViewModel, calendarViewModel: CalendarViewModel, settingsViewModel: SettingsViewModel) {

    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedDate by calendarViewModel.selectedDate.collectAsState()
    val displayedMonth by calendarViewModel.displayedMonth.collectAsState()
    val datesWithData by calendarViewModel.datesWithData.collectAsState()
    val selectedForecasts by calendarViewModel.selectedForecasts.collectAsState()
    var calendarExpanded by remember { mutableStateOf(false) }
    var filterExpanded by remember { mutableStateOf(false) }
    var switchIsLastPressed by remember { mutableStateOf(false) }
    var dayIsLastPressed by remember { mutableStateOf(false) }
    val weatherSettings by settingsViewModel.weatherSettings.collectAsState()
    var isFilterEnabled by rememberSaveable { mutableStateOf(false) }
    val dayOfMonth = selectedDate.dayOfMonth
    val formattedDay = if (dayOfMonth < 10) "$dayOfMonth." else "$dayOfMonth."

    LaunchedEffect(Unit) {
        calendarViewModel.observeWeather(viewModel.weatherState)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(RocketBoyTheme.colors.background)),
    ) {
        if (errorMessage != null) { // Error message if screen fail
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                NoConnectionCard()
            }
        } else {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .absolutePadding(top = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$formattedDay ${selectedDate.format(DateTimeFormatter.ofPattern("MMM yyyy"))}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF172B44),
                            modifier = Modifier.weight(1f)
                        )

                        if (calendarExpanded) {
                            IconButton(onClick = { calendarViewModel.changeMonth(-1) }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Month"
                                )
                            }
                            IconButton(onClick = { calendarViewModel.changeMonth(1) }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Month"
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                dayIsLastPressed = false
                                calendarExpanded = !calendarExpanded
                                if (calendarExpanded) filterExpanded = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = if (calendarExpanded) "Hide calendar" else "Show calendar",
                                tint = Color(0xFF172B44)
                            )
                        }
                        IconButton(
                            onClick = {
                                switchIsLastPressed = false
                                filterExpanded = !filterExpanded
                                if (filterExpanded) calendarExpanded = false
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.filter),
                                contentDescription = if (calendarExpanded) "Hide calendar" else "Show calendar",
                                tint = Color(0xFF172B44)
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = filterExpanded,
                        exit = fadeOut(animationSpec = tween(delayMillis = if(switchIsLastPressed) 150 else 0)) + shrinkVertically(animationSpec = tween(delayMillis = if(switchIsLastPressed) 150 else 0))
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Switch(
                                checked = isFilterEnabled,
                                onCheckedChange = {
                                    switchIsLastPressed = true
                                    isFilterEnabled = it
                                    filterExpanded = !filterExpanded
                                                  },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFF1FAEE),
                                    uncheckedThumbColor = Color(0xFF172B44),
                                    checkedTrackColor = Color(0xFF172B44),
                                    uncheckedTrackColor = Color(0xFFF1FAEE)
                                ),
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Hide Unsuitable Timeslots",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFF172B44),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = calendarExpanded,
                        exit = fadeOut(animationSpec = tween(delayMillis = if(dayIsLastPressed) 150 else 0)) + shrinkVertically(animationSpec = tween(delayMillis = if(dayIsLastPressed) 150 else 0))
                    ) {
                        Column {
                            CalendarGrid(
                                month = displayedMonth,
                                selectedDate = selectedDate,
                                onDateSelected = {
                                    dayIsLastPressed = true
                                    calendarViewModel.selectDate(it)
                                    if (it in datesWithData) {
                                        calendarExpanded = false
                                    }
                                },
                                datesWithData = datesWithData
                            )

                            //Spacer(modifier = Modifier.height(12.dp))

                            HorizontalDivider(
                                color = Color(0xFF1D3556),
                                thickness = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        val ratedForecasts = selectedForecasts.mapNotNull { forecast ->
                            weatherSettings?.let { settings ->
                                val rating = getRating(forecast.data, settings)
                                if (!isFilterEnabled || rating > 0) {
                                    RatedForecast(forecast, settings, rating)
                                } else {
                                    null
                                }
                            }
                        }

                        if (ratedForecasts.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(ratedForecasts) { rated ->
                                    LocationforecastCard(
                                        timeSeries = rated.forecast,
                                        settings = rated.settings,
                                        rating = rated.rating
                                    )
                                }
                            }
                        } else {
                            if (selectedDate !in datesWithData) {
                                NoDataCard()
                            } else if (isFilterEnabled) {
                                NoSuitableDataCard()
                            } else {
                                NoDataCard()
                            }
                        }
                    }
                }
            }
        }
    }
}

