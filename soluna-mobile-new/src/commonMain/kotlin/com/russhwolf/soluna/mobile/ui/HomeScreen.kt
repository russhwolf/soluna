package com.russhwolf.soluna.mobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.mobile.graphics.SunMoonTimesGraphic
import com.russhwolf.soluna.mobile.theme.SolunaTheme
import com.russhwolf.soluna.mobile.util.formatDate
import com.russhwolf.soluna.mobile.util.formatTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs

@Composable
fun HomeScreen(
    state: HomeScreenState,
    onEvent: (HomeScreenEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold {
        when (state) {
            is HomeScreenState.Initial -> {
                Box(modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            HomeScreenState.NoLocationSelected -> {
                Box(modifier.fillMaxSize()) {
                    // TODO design more interesting empty state
                    Text("No location selected", Modifier.align(Alignment.Center))
                }
            }

            is HomeScreenState.Populated -> {
                HomeScreenPopulated(state, onEvent, modifier)
            }
        }
    }
}

@Composable
fun HomeScreenPopulated(
    state: HomeScreenState.Populated,
    onEvent: (HomeScreenEvent) -> Unit,
    modifier: Modifier
) {
    Box(Modifier.fillMaxSize()) {
        Column(modifier = modifier.align(Alignment.TopCenter).padding(16.dp)) {
            Row {
                val text = state.sunTimes.toStrings("Sun", state.timeZone)
                Text(
                    text.first,
                    Modifier.weight(if (text.first.isEmpty()) 0f else 1f),
                    style = SolunaTheme.typography.bodyLarge
                )
                Text(
                    text.second,
                    Modifier.weight(if (text.second.isEmpty()) 0f else 1f),
                    style = SolunaTheme.typography.bodyLarge
                )
            }
            Row {
                val text = state.moonTimes.toStrings("Moon", state.timeZone)
                Text(
                    text.first,
                    Modifier.weight(if (text.first.isEmpty()) 0f else 1f),
                    style = SolunaTheme.typography.bodyLarge
                )
                Text(
                    text.second,
                    Modifier.weight(if (text.second.isEmpty()) 0f else 1f),
                    style = SolunaTheme.typography.bodyLarge
                )
            }
            SunMoonTimesGraphic(
                state = state.sunMoonTimesGraphicState
            )
            Column(Modifier.align(Alignment.CenterHorizontally)) {
                val modeText = when (state) {
                    is HomeScreenState.Daily -> "Showing times for ${state.date.formatDate()}."
                    is HomeScreenState.Next -> "Showing upcoming times."
                }
                Text(
                    text = modeText,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = SolunaTheme.typography.bodyMedium
                )

                val latitudeDirection = if (state.latitude >= 0) "N" else "S"
                val longitudeDirection = if (state.longitude >= 0) "E" else "W"
                Text(
                    text = "Location: ${state.locationName} (${abs(state.latitude)}°$latitudeDirection, ${abs(state.longitude)}°$longitudeDirection)",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = SolunaTheme.typography.bodyMedium,
                )
                Text(
                    text = "Time zone: ${state.timeZone.id}",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = SolunaTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.weight(1f))
            OutlinedButton(
                content = { Text(state.locationName) },
                onClick = { onEvent(HomeScreenEvent.LocationListVisibilityChange(true)) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            DropdownMenu(
                expanded = state.locationListVisible,
                onDismissRequest = { onEvent(HomeScreenEvent.LocationListVisibilityChange(false)) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                state.locationList.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location.label) },
                        onClick = {
                            onEvent(HomeScreenEvent.DisplayedLocationChange(location.id))
                            onEvent(HomeScreenEvent.LocationListVisibilityChange(false))
                        }
                    )
                }
            }
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                TextButton(
                    content = { Text("Show Upcoming Times") },
                    onClick = { onEvent(HomeScreenEvent.ModeChange(HomeScreenState.Mode.Next)) }
                )
                TextButton(
                    content = { Text("Select Date") },
                    onClick = { onEvent(HomeScreenEvent.DatePickerVisibilityChange(true)) }
                )
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        if (state.datePickerVisible) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                confirmButton = {
                    TextButton(
                        content = { Text("Use Selected Date") },
                        onClick = {
                            val selectedMillis = datePickerState.selectedDateMillis
                                ?: return@TextButton // TODO feedback to user about unselected date?
                            val selectedLocalDate =
                                Instant.fromEpochMilliseconds(selectedMillis).toLocalDateTime(state.timeZone).date
                            onEvent(HomeScreenEvent.ModeChange(HomeScreenState.Mode.Daily(selectedLocalDate)))
                            onEvent(HomeScreenEvent.DatePickerVisibilityChange(false))
                        }
                    )
                },
                dismissButton = {
                    TextButton(
                        content = { Text("Cancel") },
                        onClick = { onEvent(HomeScreenEvent.DatePickerVisibilityChange(false)) }
                    )
                },
                onDismissRequest = { onEvent(HomeScreenEvent.DatePickerVisibilityChange(false)) },

                ) {
                DatePicker(datePickerState)
            }
        }
    }
}

// TODO move to resources and don't depend on concatenation
@Composable
private fun RiseSetResult<Instant>.toStrings(body: String, timeZone: TimeZone): Pair<String, String> = when (this) {
    is RiseSetResult.RiseThenSet -> "${body}rise: ${riseTime.formatTime(timeZone)}" to "${body}set: ${
        setTime.formatTime(
            timeZone
        )
    }"

    is RiseSetResult.SetThenRise -> "${body}set: ${setTime.formatTime(timeZone)}" to "${body}rise: ${
        riseTime.formatTime(
            timeZone
        )
    }"

    is RiseSetResult.RiseOnly -> "${body}rise: ${riseTime.formatTime(timeZone)}" to "No ${body}rise"
    is RiseSetResult.SetOnly -> "${body}set: ${setTime.formatTime(timeZone)}" to "No ${body}set"
    RiseSetResult.UpAllDay -> "Up all day" to ""
    RiseSetResult.DownAllDay -> "Down all day" to ""
    RiseSetResult.Unknown -> "Unknown" to ""
}

