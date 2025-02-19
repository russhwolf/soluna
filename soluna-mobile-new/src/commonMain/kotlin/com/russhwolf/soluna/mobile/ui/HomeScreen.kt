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
import org.jetbrains.compose.resources.stringResource
import soluna.soluna_mobile_new.generated.resources.Res
import soluna.soluna_mobile_new.generated.resources.abbr_east
import soluna.soluna_mobile_new.generated.resources.abbr_north
import soluna.soluna_mobile_new.generated.resources.abbr_south
import soluna.soluna_mobile_new.generated.resources.abbr_west
import soluna.soluna_mobile_new.generated.resources.action_cancel
import soluna.soluna_mobile_new.generated.resources.home_about_date
import soluna.soluna_mobile_new.generated.resources.home_about_location
import soluna.soluna_mobile_new.generated.resources.home_about_timezone
import soluna.soluna_mobile_new.generated.resources.home_about_upcoming
import soluna.soluna_mobile_new.generated.resources.home_button_date
import soluna.soluna_mobile_new.generated.resources.home_button_upcoming
import soluna.soluna_mobile_new.generated.resources.home_dialog_button_date
import soluna.soluna_mobile_new.generated.resources.home_empty
import soluna.soluna_mobile_new.generated.resources.home_moon_down
import soluna.soluna_mobile_new.generated.resources.home_moon_up
import soluna.soluna_mobile_new.generated.resources.home_moonrise
import soluna.soluna_mobile_new.generated.resources.home_moonset
import soluna.soluna_mobile_new.generated.resources.home_no_moonrise
import soluna.soluna_mobile_new.generated.resources.home_no_moonset
import soluna.soluna_mobile_new.generated.resources.home_no_sunrise
import soluna.soluna_mobile_new.generated.resources.home_no_sunset
import soluna.soluna_mobile_new.generated.resources.home_sun_down
import soluna.soluna_mobile_new.generated.resources.home_sun_up
import soluna.soluna_mobile_new.generated.resources.home_sunrise
import soluna.soluna_mobile_new.generated.resources.home_sunset
import soluna.soluna_mobile_new.generated.resources.location_coordinate
import soluna.soluna_mobile_new.generated.resources.unknown
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
                    Text(stringResource(Res.string.home_empty), Modifier.align(Alignment.Center))
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
                val text = state.sunTimes.toSunStrings(state.timeZone)
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
                val text = state.moonTimes.toMoonStrings(state.timeZone)
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
                    is HomeScreenState.Daily -> stringResource(Res.string.home_about_date, state.date.formatDate())
                    is HomeScreenState.Next -> stringResource(Res.string.home_about_upcoming)
                }
                Text(
                    text = modeText,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = SolunaTheme.typography.bodyMedium
                )

                // TODO this may not localize well
                val latitudeDirection = if (state.latitude >= 0) Res.string.abbr_north else Res.string.abbr_south
                val longitudeDirection = if (state.longitude >= 0) Res.string.abbr_east else Res.string.abbr_west
                val latitudeText = stringResource(
                    Res.string.location_coordinate,
                    abs(state.latitude),
                    stringResource(latitudeDirection)
                )
                val longitudeText = stringResource(
                    Res.string.location_coordinate,
                    abs(state.longitude),
                    stringResource(longitudeDirection)
                )
                Text(
                    text = stringResource(
                        Res.string.home_about_location,
                        state.locationName,
                        latitudeText,
                        longitudeText
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = SolunaTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(Res.string.home_about_timezone, state.timeZone.id),
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
                    content = { Text(stringResource(Res.string.home_button_upcoming)) },
                    onClick = { onEvent(HomeScreenEvent.ModeChange(HomeScreenState.Mode.Next)) }
                )
                TextButton(
                    content = { Text(stringResource(Res.string.home_button_date)) },
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
                        content = { Text(stringResource(Res.string.home_dialog_button_date)) },
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
                        content = { Text(stringResource(Res.string.action_cancel)) },
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

@Composable
private fun RiseSetResult<Instant>.toSunStrings(timeZone: TimeZone): Pair<String, String> = when (this) {
    is RiseSetResult.RiseThenSet -> stringResource(Res.string.home_sunrise, riseTime.formatTime(timeZone)) to
            stringResource(Res.string.home_sunset, setTime.formatTime(timeZone))

    is RiseSetResult.SetThenRise -> stringResource(Res.string.home_sunset, setTime.formatTime(timeZone)) to
            stringResource(Res.string.home_sunrise, riseTime.formatTime(timeZone))

    is RiseSetResult.RiseOnly -> stringResource(Res.string.home_sunrise, riseTime.formatTime(timeZone)) to
            stringResource(Res.string.home_no_sunset)

    is RiseSetResult.SetOnly -> stringResource(Res.string.home_sunset, setTime.formatTime(timeZone)) to
            stringResource(Res.string.home_no_sunrise)

    RiseSetResult.UpAllDay -> stringResource(Res.string.home_sun_up) to ""
    RiseSetResult.DownAllDay -> stringResource(Res.string.home_sun_down) to ""
    RiseSetResult.Unknown -> stringResource(Res.string.unknown) to ""
}

@Composable
private fun RiseSetResult<Instant>.toMoonStrings(timeZone: TimeZone): Pair<String, String> = when (this) {
    is RiseSetResult.RiseThenSet -> stringResource(Res.string.home_moonrise, riseTime.formatTime(timeZone)) to
            stringResource(Res.string.home_moonset, setTime.formatTime(timeZone))

    is RiseSetResult.SetThenRise -> stringResource(Res.string.home_moonset, setTime.formatTime(timeZone)) to
            stringResource(Res.string.home_moonrise, riseTime.formatTime(timeZone))

    is RiseSetResult.RiseOnly -> stringResource(Res.string.home_moonrise, riseTime.formatTime(timeZone)) to
            stringResource(Res.string.home_no_moonset)

    is RiseSetResult.SetOnly -> stringResource(Res.string.home_moonset, setTime.formatTime(timeZone)) to
            stringResource(Res.string.home_no_moonrise)

    RiseSetResult.UpAllDay -> stringResource(Res.string.home_moon_up) to ""
    RiseSetResult.DownAllDay -> stringResource(Res.string.home_moon_down) to ""
    RiseSetResult.Unknown -> stringResource(Res.string.unknown) to ""
}

