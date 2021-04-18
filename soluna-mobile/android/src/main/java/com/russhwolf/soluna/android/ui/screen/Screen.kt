package com.russhwolf.soluna.android.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
inline fun <reified VM : BaseViewModel<State, Event, Action>, State : Any, Event : Any, Action : Any> Screen(
    viewModel: VM,
    crossinline onEvent: (event: Event) -> Unit,
    content: @Composable (state: State, performAction: (action: Action) -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareState = remember(viewModel.state, lifecycleOwner) {
        viewModel.state.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    val lifecycleAwareEvents = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    LaunchedEffect(viewModel) {
        launch { lifecycleAwareEvents.collect { onEvent(it) } }
    }
    val state = lifecycleAwareState.collectAsState(viewModel.state.value)
    DisposableEffect(viewModel) {
        viewModel.activate()
        onDispose {
            viewModel.dispose()
        }
    }
    content(state.value) { action -> scope.launch { viewModel.performAction(action) } }
}
