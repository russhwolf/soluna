package com.russhwolf.soluna.mobile.repository

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import com.russhwolf.soluna.mobile.suspendTest
import io.ktor.client.features.logging.EMPTY
import io.ktor.client.features.logging.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GeocodeRepositoryTest {
    private val googleApiClient = GoogleApiClient.Impl(
        createGeocodeMockClientEngine(
            mapOf(
                "Test+Location" to GeocodeData(
                    latitude = 42.3956001,
                    longitude = -71.1387674,
                    timeZone = "America/New_York"
                )
            )
        ),
        Logger.EMPTY
    )

    private val repository = GeocodeRepository.Impl(googleApiClient, FakeCurrentTimeRepository())

    @Test
    fun geocodeLocation_valid() = suspendTest {
        val geocodeData = repository.geocodeLocation("Test Location")

        assertEquals(
            expected = GeocodeData(
                latitude = 42.3956001,
                longitude = -71.1387674,
                timeZone = "America/New_York"
            ),
            actual = geocodeData
        )
    }

    @Test
    fun geocodeLocation_invalid() = suspendTest {
        val geocodeData = repository.geocodeLocation("Bad Location")

        assertNull(geocodeData)
    }
}
