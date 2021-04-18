package com.russhwolf.soluna.android.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
inline fun <reified VM : BaseViewModel<State, Event, Action>, State : Any, Event : Any, Action : Any> Screen(
    viewModel: VM,
    navDestination: NavDestination?,
    crossinline onEvent: (event: Event) -> Unit,
    content: @Composable (state: State, performAction: (action: Action) -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(navDestination) {
        launch { viewModel.events.collect { onEvent(it) } }
    }
    val state = viewModel.state.collectAsState()
    DisposableEffect(navDestination) {
        viewModel.activate()
        onDispose {
            viewModel.dispose()
        }
    }
    content(state.value) { action -> scope.launch { viewModel.performAction(action) } }
}
