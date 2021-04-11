package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.screen.BaseViewModel
import com.russhwolf.soluna.mobile.util.SupervisorScope
import kotlinx.coroutines.Dispatchers

class NativeViewModel<State : Any, Event : Any, Action : Any>(private val delegate: BaseViewModel<State, Event, Action>) {
    private val scope = SupervisorScope(Dispatchers.Main)

    val initialState = delegate.initialState

    val state = FlowAdapter(scope, delegate.state)
    val events = FlowAdapter(scope, delegate.events)

    fun performAction(action: Action) = SuspendAdapter(scope) { delegate.performAction(action) }

    fun activate() {
        delegate.activate()
    }

    fun dispose() {
        scope.clear()
        delegate.dispose()
    }
}
