package com.russhwolf.soluna.android.ui.screen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.russhwolf.soluna.android.R
import com.russhwolf.soluna.android.extensions.toDisplayTime
import com.russhwolf.soluna.android.ui.components.SunMoonTimesGraphic
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import com.russhwolf.soluna.mobile.screen.home.HomeViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.math.abs

@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) =
    Screen(
        viewModel,
        onEvent = { event ->
            when (event) {
                HomeViewModel.Event.Settings -> navController.navigate(Destination.Settings)
            }
        }
    ) { state, performAction ->
        HomeScreenContent(
            state = state,
            onSettingsClick = { performAction(HomeViewModel.Action.Settings) }
        )
    }

@Composable
private fun HomeScreenContent(
    state: HomeViewModel.State,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeAppBar(
                state = state,
                onSettingsClick = onSettingsClick
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            when (state) {
                HomeViewModel.State.Loading -> LoadingContent()
                HomeViewModel.State.NoLocationSelected -> EmptyContent()
                is HomeViewModel.State.Populated -> {
                    PopulatedContent(state)
                }
            }
        }
    }
}

@Composable
private fun HomeAppBar(
    state: HomeViewModel.State,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            val title = if (state is HomeViewModel.State.Populated) {
                state.locationName
            } else {
                stringResource(R.string.title_home)
            }
            Text(title, style = SolunaTheme.typography.h6)
        },
        actions = {
            IconButton(
                content = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.home_menu_settings)
                    )
                },
                onClick = onSettingsClick
            )
        }
    )
}

@Composable
private fun ColumnScope.LoadingContent() {
    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ColumnScope.EmptyContent() {
    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.home_content_empty))
    }
}

@Composable
private fun ColumnScope.PopulatedContent(state: HomeViewModel.State.Populated) {
    @Composable
    fun RowScope.PortraitColumn(content: @Composable () -> Unit) {
        if (LocalConfiguration.current.orientation == ORIENTATION_PORTRAIT) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                content()
            }
        } else {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                content()
            }
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PortraitColumn {
            Text(stringResource(R.string.home_times_sunrise, state.sunriseTime.toDisplayTimeOrNone(state.timeZone)))
            Text(stringResource(R.string.home_times_sunset, state.sunsetTime.toDisplayTimeOrNone(state.timeZone)))
        }
        PortraitColumn {
            Text(stringResource(R.string.home_times_moonrise, state.moonriseTime.toDisplayTimeOrNone(state.timeZone)))
            Text(stringResource(R.string.home_times_moonset, state.moonsetTime.toDisplayTimeOrNone(state.timeZone)))
        }
    }

    SunMoonTimesGraphic(
        currentTime = state.currentTime,
        sunriseTime = state.sunriseTime,
        sunsetTime = state.sunsetTime,
        moonriseTime = state.moonriseTime,
        moonsetTime = state.moonsetTime,
        timeZone = state.timeZone,
        modifier =
        if (LocalConfiguration.current.orientation == ORIENTATION_PORTRAIT) {
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        } else {
            Modifier
                .fillMaxHeight()
                .wrapContentWidth()
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        stringResource(R.string.home_coordinates, getDisplayCoordinates(state.latitude, state.longitude)),
        style = SolunaTheme.typography.caption,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Text(
        stringResource(R.string.home_timezone, state.timeZone.id),
        style = SolunaTheme.typography.caption,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(
        modifier = Modifier
            .weight(1f)
            .defaultMinSize(minHeight = 16.dp)
    )
}

@Composable
private fun getDisplayCoordinates(latitude: Double, longitude: Double): String {
    val latitudeDirectionResource = if (latitude > 0) R.string.location_abbr_north else R.string.location_abbr_south
    val longitudeDirectionResource = if (longitude > 0) R.string.location_abbr_east else R.string.location_abbr_west
    return stringResource(
        R.string.location_format_pair,
        abs(latitude),
        stringResource(latitudeDirectionResource),
        abs(longitude),
        stringResource(longitudeDirectionResource)
    )
}

@Composable
private fun Instant?.toDisplayTimeOrNone(timeZone: TimeZone): String =
    this?.toDisplayTime(timeZone) ?: stringResource(R.string.home_times_none)

internal class HomeStateProvider : PreviewParameterProvider<HomeViewModel.State> {
    private val timeZone = TimeZone.of("America/New_York")

    override val values: Sequence<HomeViewModel.State> = sequenceOf(
        HomeViewModel.State.Loading,
        HomeViewModel.State.NoLocationSelected,
        HomeViewModel.State.Populated(
            locationName = "Home",
            currentTime = LocalDateTime(2021, 1, 1, 11, 0).toInstant(timeZone),
            sunriseTime = LocalDateTime(2021, 1, 1, 6, 0).toInstant(timeZone),
            sunsetTime = LocalDateTime(2021, 1, 1, 20, 0).toInstant(timeZone),
            moonriseTime = LocalDateTime(2021, 1, 1, 8, 30).toInstant(timeZone),
            moonsetTime = LocalDateTime(2021, 1, 1, 22, 0).toInstant(timeZone),
            timeZone = timeZone,
            latitude = 27.183,
            longitude = 62.832
        )
    )
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenContent_Portrait_Light(
    @PreviewParameter(provider = HomeStateProvider::class)
    state: HomeViewModel.State
) {
    SolunaTheme {
        HomeScreenContent(
            state = state,
            onSettingsClick = {}
        )
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenContent_Portrait_Dark(
    @PreviewParameter(provider = HomeStateProvider::class)
    state: HomeViewModel.State
) {
    SolunaTheme {
        HomeScreenContent(
            state = state,
            onSettingsClick = {}
        )
    }
}

// TODO awaiting better portrait preview APIs
@Preview(widthDp = 800, heightDp = 480)
@Composable
fun HomeScreenContent_Landscape_Light(
    @PreviewParameter(provider = HomeStateProvider::class)
    state: HomeViewModel.State
) {
    SolunaTheme {
        HomeScreenContent(
            state = state,
            onSettingsClick = {}
        )
    }
}

@Preview(widthDp = 800, heightDp = 480, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenContent_Landscape_Dark(
    @PreviewParameter(provider = HomeStateProvider::class)
    state: HomeViewModel.State
) {
    SolunaTheme {
        HomeScreenContent(
            state = state,
            onSettingsClick = {}
        )
    }
}

@Preview(showSystemUi = true, fontScale = 2f)
@Composable
fun HomeScreenContent_Portrait_Light_LargeText(
    @PreviewParameter(provider = HomeStateProvider::class)
    state: HomeViewModel.State
) {
    SolunaTheme {
        HomeScreenContent(
            state = state,
            onSettingsClick = {}
        )
    }
}

@Preview(showSystemUi = true, fontScale = 2f, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenContent_Portrait_Dark_LargeText(
    @PreviewParameter(provider = HomeStateProvider::class)
    state: HomeViewModel.State
) {
    SolunaTheme {
        HomeScreenContent(
            state = state,
            onSettingsClick = {}
        )
    }
}


