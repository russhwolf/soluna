package com.russhwolf.soluna.mobile.screen.addlocation

import com.russhwolf.soluna.mobile.GeocodeData
import com.russhwolf.soluna.mobile.MockSolunaRepository
import com.russhwolf.soluna.mobile.runBlockingTest
import com.russhwolf.soluna.mobile.screen.AbstractViewModelTest
import com.russhwolf.soluna.mobile.util.EventTrigger
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals


class AddLocationViewModelTest : AbstractViewModelTest<AddLocationViewModel, AddLocationViewState>() {
    private val repository = MockSolunaRepository(geocodeMap = mapOf("Home" to GeocodeData(27.18, 62.83, "UTC")))

    override suspend fun createViewModel(): AddLocationViewModel =
        AddLocationViewModel(repository, Dispatchers.Unconfined)

    @Test
    fun addLocation_valid() = runBlockingTest {
        viewModel.addLocation("Home", "27.18", "62.83", "UTC").await()
        val expectedState = AddLocationViewState(exitTrigger = EventTrigger.create())
        assertEquals(expectedState, state)
    }

    @Test
    fun addLocation_invalid() = runBlockingTest {
        viewModel.addLocation("Home", "Foo", "62.83", "UTC").await()
        val expectedState = AddLocationViewState(exitTrigger = EventTrigger.empty())
        assertEquals(expectedState, state)
    }

    @Test
    fun geocodeLocation_valid() = runBlockingTest {
        viewModel.geocodeLocation("Home").await()
        val expectedState = AddLocationViewState(geocodeTrigger = EventTrigger.create(GeocodeData(27.18, 62.83, "UTC")))
        assertEquals(expectedState, state)
    }

    @Test
    fun geocodeLocation_invalid() = runBlockingTest {
        viewModel.geocodeLocation("Away").await()
        val expectedState = AddLocationViewState(geocodeTrigger = EventTrigger.empty())
        assertEquals(expectedState, state)
    }
}
