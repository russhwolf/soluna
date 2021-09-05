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
    val rememberedViewModel = remember { viewModel }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareState = remember(rememberedViewModel.state, lifecycleOwner) {
        rememberedViewModel.state.flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    val lifecycleAwareEvents = remember(rememberedViewModel.events, lifecycleOwner) {
        rememberedViewModel.events.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    LaunchedEffect(rememberedViewModel) {
        launch { lifecycleAwareEvents.collect { onEvent(it) } }
    }
    val state = lifecycleAwareState.collectAsState(rememberedViewModel.state.value)
    DisposableEffect(rememberedViewModel) {
        rememberedViewModel.activate()
        onDispose {
            rememberedViewModel.dispose()
        }
    }
    content(state.value) { action -> scope.launch { rememberedViewModel.performAction(action) } }
}
