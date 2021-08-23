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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.russhwolf.soluna.android.extensions.toDisplayTime
import com.russhwolf.soluna.android.ui.SunMoonTimesGraphic
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import com.russhwolf.soluna.mobile.screen.home.HomeViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) =
    Screen(
        viewModel,
        onEvent = { event ->
            when (event) {
                HomeViewModel.Event.Settings -> navController.navigate(Destination.Settings)
            }
        }) { state, performAction ->
        HomeScreenContent(state, performAction)
    }

@Composable
private fun HomeScreenContent(
    state: HomeViewModel.State,
    performAction: (action: HomeViewModel.Action) -> Unit
) {
    Scaffold(
        topBar = { HomeAppBar(state, performAction) }
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
    performAction: (action: HomeViewModel.Action) -> Unit
) {
    TopAppBar(
        title = {
            val title = if (state is HomeViewModel.State.Populated) state.locationName else "Soluna"
            Text(title, style = SolunaTheme.typography.h6)
        },
        actions = {
            IconButton(
                content = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                },
                onClick = { performAction(HomeViewModel.Action.Settings) }
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
        Text("No location selected!")
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
            .padding(vertical = 16.dp)) {
        PortraitColumn {
            Text(
                "Sunrise: ${state.sunriseTime?.toDisplayTime(state.timeZone) ?: "None"}",
                Modifier.padding(horizontal = 16.dp)
            )
            Text(
                "Sunset: ${state.sunsetTime?.toDisplayTime(state.timeZone) ?: "None"}",
                Modifier.padding(horizontal = 16.dp)
            )
        }
        PortraitColumn {
            Text(
                "Moonrise: ${state.moonriseTime?.toDisplayTime(state.timeZone) ?: "None"}",
                Modifier.padding(horizontal = 16.dp)
            )
            Text(
                "Moonset: ${state.moonsetTime?.toDisplayTime(state.timeZone) ?: "None"}",
                Modifier.padding(horizontal = 16.dp)
            )
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

    Text(
        "Using time zone ${state.timeZone.id}",
        style = SolunaTheme.typography.caption,
        modifier = Modifier.padding(16.dp)
    )

    Spacer(modifier = Modifier.weight(1f))
}

class PopulatedStateProvider : PreviewParameterProvider<HomeViewModel.State.Populated> {
    private val timeZone = TimeZone.of("America/New_York")

    override val values: Sequence<HomeViewModel.State.Populated> = sequenceOf(
        HomeViewModel.State.Populated(
            locationName = "Home",
            currentTime = LocalDateTime(2021, 1, 1, 11, 0).toInstant(timeZone),
            sunriseTime = LocalDateTime(2021, 1, 1, 6, 0).toInstant(timeZone),
            sunsetTime = LocalDateTime(2021, 1, 1, 20, 0).toInstant(timeZone),
            moonriseTime = LocalDateTime(2021, 1, 1, 8, 30).toInstant(timeZone),
            moonsetTime = LocalDateTime(2021, 1, 1, 22, 0).toInstant(timeZone),
            timeZone = timeZone
        )
    )
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenContent_Loading_Light() {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = HomeViewModel.State.Loading) {}
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenContent_Loading_Dark() {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = HomeViewModel.State.Loading) {}
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenContent_NoLocationSelected_Light() {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = HomeViewModel.State.NoLocationSelected) {}
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenContent_NoLocationSelected_Dark() {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = HomeViewModel.State.NoLocationSelected) {}
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenContent_Populated_Portrait_Light(
    @PreviewParameter(provider = PopulatedStateProvider::class)
    state: HomeViewModel.State.Populated
) {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = state) {}
        }
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenContent_Populated_Portrait_Dark(
    @PreviewParameter(provider = PopulatedStateProvider::class)
    state: HomeViewModel.State.Populated
) {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = state) {}
        }
    }
}

// TODO awaiting better portrait preview APIs
@Preview(widthDp = 800, heightDp = 480)
@Composable
fun HomeScreenContent_Populated_Landscape_Light(
    @PreviewParameter(provider = PopulatedStateProvider::class)
    state: HomeViewModel.State.Populated
) {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = state) {}
        }
    }
}

@Preview(widthDp = 800, heightDp = 480, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenContent_Populated_Landscape_Dark(
    @PreviewParameter(provider = PopulatedStateProvider::class)
    state: HomeViewModel.State.Populated
) {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = state) {}
        }
    }
}

@Preview(showSystemUi = true, fontScale = 2f)
@Composable
fun HomeScreenContent_Populated_Portrait_Light_LargeText(
    @PreviewParameter(provider = PopulatedStateProvider::class)
    state: HomeViewModel.State.Populated
) {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = state) {}
        }
    }
}

@Preview(showSystemUi = true, fontScale = 2f, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenContent_Populated_Portrait_Dark_LargeText(
    @PreviewParameter(provider = PopulatedStateProvider::class)
    state: HomeViewModel.State.Populated
) {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreenContent(state = state) {}
        }
    }
}


