@file:Suppress("unused") // This class is called from Swift

package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.screen.BaseViewModel

class NativeViewModel<State : Any, Event : Any, Action : Any>(private val delegate: BaseViewModel<State, Event, Action>) {
    val initialState get() = delegate.state.value

    val state = FlowAdapter(delegate.coroutineScope, delegate.state)
    val events = FlowAdapter(delegate.coroutineScope, delegate.events)

    fun performAction(action: Action) = SuspendAdapter(delegate.coroutineScope) { delegate.performAction(action) }

    fun activate() {
        delegate.activate()
    }

    fun dispose() {
        delegate.dispose()
    }
}
