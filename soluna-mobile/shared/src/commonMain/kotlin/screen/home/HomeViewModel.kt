package com.russhwolf.soluna.mobile.screen.home

import com.russhwolf.soluna.mobile.repository.UpcomingTimes
import com.russhwolf.soluna.mobile.repository.UpcomingTimesRepository
import com.russhwolf.soluna.mobile.screen.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    private val upcomingTimesRepository: UpcomingTimesRepository,
    dispatcher: CoroutineDispatcher
) : BaseViewModel<HomeViewModel.State, HomeViewModel.Event, HomeViewModel.Action>(
    State.Loading,
    dispatcher
) {
    init {
        upcomingTimesRepository.getUpcomingTimes()
            .map {
                if (it == null) {
                    State.NoLocationSelected
                } else {
                    State.Populated(it)
                }
            }
            .onEach { emitState(it) }
            .launchIn(coroutineScope)
    }

    override suspend fun performAction(action: Action) {
    }


    sealed class State {
        object Loading : State()
        object NoLocationSelected : State()
        data class Populated(val upcomingTimes: UpcomingTimes) : State()
    }

    sealed class Event {

    }

    sealed class Action {

    }
}
