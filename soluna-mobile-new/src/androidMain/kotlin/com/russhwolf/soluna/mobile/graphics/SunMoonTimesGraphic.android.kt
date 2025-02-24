package com.russhwolf.soluna.mobile.graphics

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.russhwolf.soluna.RiseSetResult
import com.russhwolf.soluna.mobile.theme.SolunaTheme
import com.russhwolf.soluna.time.InstantAstronomicalCalculator
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SunTimesGraphic_Daily(
    @PreviewParameter(TimesResultPreviewProviderDaily::class)
    times: Pair<RiseSetResult<Instant>, RiseSetResult<Instant>>
) {
    val date = LocalDate(2000, 1, 1)
    val (sunTimes, moonTimes) = times
    SolunaTheme {
        Surface {
            SunMoonTimesGraphic(
                SunMoonTimesGraphicState.Daily(
                    date,
                    sunTimes,
                    moonTimes,
                    TimeZone.UTC
                ),
                modifier = Modifier
                    .width(300.dp)
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun SunTimesGraphic_Next(
    @PreviewParameter(TimesResultPreviewProviderNext::class)
    times: Pair<RiseSetResult<Instant>, RiseSetResult<Instant>>
) {
    val currentTime = LocalDateTime(2000, 1, 1, 12, 30).toInstant(TimeZone.UTC)
    val (sunTimes, moonTimes) = times
    SolunaTheme {
        Surface {
            SunMoonTimesGraphic(
                SunMoonTimesGraphicState.Next(
                    currentTime,
                    sunTimes,
                    moonTimes,
                    TimeZone.UTC
                ),
                modifier = Modifier
                    .width(300.dp)
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun SunTimesGraphic_DST() {
    val timeZone = TimeZone.of("America/New_York")
    val currentTime = LocalDateTime(2019, 3, 10, 6, 28).toInstant(timeZone)
    val calculator = InstantAstronomicalCalculator(
        date = currentTime.toLocalDateTime(timeZone).date,
        zone = timeZone,
        latitude = 42.388,
        longitude = -71.100
    )
    SolunaTheme {
        Surface {
            SunMoonTimesGraphic(
                SunMoonTimesGraphicState.Next(
                    currentTime,
                    calculator.sunTimes,
                    calculator.moonTimes,
                    timeZone
                ),
                modifier = Modifier
                    .width(300.dp)
                    .padding(16.dp)
            )
        }
    }
}

private class TimesResultPreviewProviderDaily :
    PreviewParameterProvider<Pair<RiseSetResult<Instant>, RiseSetResult<Instant>>> {
    override val values: Sequence<Pair<RiseSetResult<Instant>, RiseSetResult<Instant>>> = sequenceOf(
        RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 1, 8, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC)
        ) to RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 1, 8, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 1, 4, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 1, 10, 0).toInstant(TimeZone.UTC)
        ) to RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 1, 4, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 1, 10, 0).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 1, 14, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC)
        ) to RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 1, 14, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 1, 8, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 2, 0).toInstant(TimeZone.UTC)
        ) to RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 1, 8, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 2, 0).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 1, 14, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 2, 0).toInstant(TimeZone.UTC)
        ) to RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 1, 14, 30).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 2, 0).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.RiseOnly(
            LocalDateTime(2000, 1, 1, 8, 30).toInstant(TimeZone.UTC),
        ) to RiseSetResult.SetOnly(
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.UpAllDay to RiseSetResult.DownAllDay
    )
}


private class TimesResultPreviewProviderNext :
    PreviewParameterProvider<Pair<RiseSetResult<Instant>, RiseSetResult<Instant>>> {
    override val values: Sequence<Pair<RiseSetResult<Instant>, RiseSetResult<Instant>>> = sequenceOf(
        RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 8, 30).toInstant(TimeZone.UTC)
        ) to RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 8, 30).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 1, 10, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 4, 30).toInstant(TimeZone.UTC)
        ) to RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 1, 10, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 4, 30).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 14, 30).toInstant(TimeZone.UTC)
        ) to RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 14, 30).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 2, 2, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 8, 30).toInstant(TimeZone.UTC)
        ) to RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 2, 2, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 8, 30).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.SetThenRise(
            LocalDateTime(2000, 1, 2, 2, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 14, 30).toInstant(TimeZone.UTC)
        ) to RiseSetResult.RiseThenSet(
            LocalDateTime(2000, 1, 2, 2, 0).toInstant(TimeZone.UTC),
            LocalDateTime(2000, 1, 2, 14, 30).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.RiseOnly(
            LocalDateTime(2000, 1, 2, 8, 30).toInstant(TimeZone.UTC),
        ) to RiseSetResult.SetOnly(
            LocalDateTime(2000, 1, 1, 19, 0).toInstant(TimeZone.UTC)
        ),
        RiseSetResult.UpAllDay to RiseSetResult.DownAllDay
    )
}
