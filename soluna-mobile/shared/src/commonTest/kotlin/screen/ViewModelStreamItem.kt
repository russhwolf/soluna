package com.russhwolf.soluna.mobile.screen

import app.cash.turbine.TurbineTestContext
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.test.assertTrue

// Helpers for asserting on viewmodel state and events in a single turbine block

sealed class ViewModelStreamItem<out State, out Event> {
    class State<out State>(val state: State) : ViewModelStreamItem<State, Nothing>()
    class Event<out Event>(val event: Event) : ViewModelStreamItem<Nothing, Event>()
}

val <State : Any, Event : Any, Action : Any> BaseViewModel<State, Event, Action>.stateAndEvents
    get() = merge(
        state.map { ViewModelStreamItem.State(it) },
        events.map { ViewModelStreamItem.Event(it) }
    )

suspend inline fun <State, Event> TurbineTestContext<ViewModelStreamItem<State, Event>>.expectViewModelState(): State {
    val item = awaitItem()
    assertTrue(item is ViewModelStreamItem.State<State>)
    return item.state
}

suspend inline fun <State, Event> TurbineTestContext<ViewModelStreamItem<State, Event>>.expectViewModelEvent(): Event {
    val item = awaitItem()
    assertTrue(item is ViewModelStreamItem.Event<Event>)
    return item.event
}
