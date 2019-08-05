package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.MockSolunaRepository
import com.russhwolf.soluna.mobile.runBlockingTest
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals


class AddLocationViewModelTest : AbstractViewModelTest<AddLocationViewModel, AddLocationViewState>() {
    private var repository = MockSolunaRepository()

    override suspend fun createViewModel(): AddLocationViewModel =
        AddLocationViewModel(repository, Dispatchers.Unconfined)

    @Test
    fun addLocation_valid() = runBlockingTest {
        viewModel.addLocation("Home", "27.18", "62.83", "UTC").await()
        val expectedState = AddLocationViewState(EventTrigger.create())
        assertEquals(expectedState, state)
    }

    @Test
    fun addLocation_invalid() = runBlockingTest {
        viewModel.addLocation("Home", "Foo", "62.83", "UTC").await()
        val expectedState = AddLocationViewState(EventTrigger.empty())
        assertEquals(expectedState, state)
    }
}
