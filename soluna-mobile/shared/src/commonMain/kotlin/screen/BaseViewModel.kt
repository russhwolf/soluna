package com.russhwolf.soluna.mobile.screen

import com.russhwolf.soluna.mobile.util.SupervisorScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * @param State The type which is emitted from this viewmodel's [state] flow
 * @param Event The type which is emitted from this viewmodel's [events] flow
 * @param Action The type which is accepted by this viewmodel's [performAction] function
 */
abstract class BaseViewModel<State : Any, Event : Any, Action : Any>(
    initialState: State,
    dispatcher: CoroutineDispatcher
) {
    protected val coroutineScope = SupervisorScope(dispatcher)

    private val mutableState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> get() = mutableState

    private val mutableEvents: MutableSharedFlow<Event> = MutableSharedFlow()
    val events: SharedFlow<Event> get() = mutableEvents

    abstract suspend fun performAction(action: Action)

    suspend fun emitState(state: State) = mutableState.emit(state)

    suspend fun emitEvent(event: Event) = mutableEvents.emit(event)

    abstract fun activate()

    fun dispose() {
        coroutineScope.clear()
    }
}
