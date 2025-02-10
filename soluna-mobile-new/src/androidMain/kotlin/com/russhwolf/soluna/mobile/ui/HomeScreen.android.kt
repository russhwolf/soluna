package com.russhwolf.soluna.mobile.ui

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.russhwolf.soluna.mobile.graphics.SunMoonTimesGraphicState
import com.russhwolf.soluna.mobile.repository.SelectableLocationSummary
import com.russhwolf.soluna.mobile.theme.SolunaTheme
import com.russhwolf.soluna.time.InstantAstronomicalCalculator
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreen_Preview_PopulatedNext() {
    SolunaTheme {
        Surface {
            val timeZone = TimeZone.of("America/New_York")
            val currentTime = LocalDateTime(2000, 1, 1, 21, 41).toInstant(timeZone)
            val latitude = 42.388
            val longitude = -71.100
            val today = currentTime.toLocalDateTime(timeZone).date
            val calculator =
                InstantAstronomicalCalculator(today.plus(1, DateTimeUnit.DAY), timeZone, latitude, longitude)
            HomeScreen(
                HomeScreenState.Next(
                    currentTime = currentTime,
                    locationName = "Somerville, MA",
                    timeZone = timeZone,
                    latitude = latitude,
                    longitude = longitude,
                    sunTimes = calculator.sunTimes,
                    moonTimes = calculator.moonTimes,
                    datePickerVisible = false,
                    locationList = listOf(SelectableLocationSummary(0, "Somerville, MA", true)),
                    locationListVisible = false,
                    sunMoonTimesGraphicState = SunMoonTimesGraphicState.Next(
                        currentTime = currentTime,
                        sunTimes = calculator.sunTimes,
                        moonTimes = calculator.moonTimes,
                        timeZone = timeZone
                    )
                )
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreen_Preview_PopulatedDaily() {
    SolunaTheme {
        Surface {
            val timeZone = TimeZone.of("America/New_York")
            val currentTime = LocalDateTime(2000, 1, 1, 21, 41).toInstant(timeZone)
            val latitude = 42.388
            val longitude = -71.100
            val today = currentTime.toLocalDateTime(timeZone).date
            val calculator = InstantAstronomicalCalculator(today, timeZone, latitude, longitude)
            HomeScreen(
                HomeScreenState.Daily(
                    date = today,
                    locationName = "Somerville, MA",
                    timeZone = timeZone,
                    latitude = latitude,
                    longitude = longitude,
                    sunTimes = calculator.sunTimes,
                    moonTimes = calculator.moonTimes,
                    datePickerVisible = false,
                    locationList = listOf(SelectableLocationSummary(0, "Somerville, MA", true)),
                    locationListVisible = false,
                    sunMoonTimesGraphicState = SunMoonTimesGraphicState.Daily(
                        date = today,
                        sunTimes = calculator.sunTimes,
                        moonTimes = calculator.moonTimes,
                        timeZone = timeZone
                    )
                )
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreen_Preview_PopulatedNext_Picker() {
    SolunaTheme {
        Surface {
            val currentTime = Clock.System.now()
            val latitude = 42.388
            val longitude = -71.100
            val timeZone = TimeZone.of("America/New_York")
            val today = currentTime.toLocalDateTime(timeZone).date
            val calculator = InstantAstronomicalCalculator(today, timeZone, latitude, longitude)
            HomeScreen(
                HomeScreenState.Next(
                    currentTime = currentTime,
                    locationName = "Somerville, MA",
                    timeZone = timeZone,
                    latitude = latitude,
                    longitude = longitude,
                    sunTimes = calculator.sunTimes,
                    moonTimes = calculator.moonTimes,
                    datePickerVisible = true,
                    locationList = listOf(SelectableLocationSummary(0, "Somerville, MA", true)),
                    locationListVisible = false,
                    sunMoonTimesGraphicState = SunMoonTimesGraphicState.Next(
                        currentTime = currentTime,
                        sunTimes = calculator.sunTimes,
                        moonTimes = calculator.moonTimes,
                        timeZone = timeZone
                    )
                )
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreen_Preview_Initial() {
    SolunaTheme {
        Surface {
            HomeScreen(HomeScreenState.Initial)
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreen_Preview_Empty() {
    SolunaTheme {
        Surface {
            HomeScreen(HomeScreenState.NoLocationSelected)
        }
    }
}
