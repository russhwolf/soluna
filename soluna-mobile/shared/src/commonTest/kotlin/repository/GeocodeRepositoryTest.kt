package com.russhwolf.soluna.mobile

import com.russhwolf.soluna.mobile.api.GoogleApiClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class GeocodeRepositoryTest {
    private val googleApiClient = GoogleApiClient.Impl(
        createMockEngine(
            latitude = 42.3956001,
            longitude = -71.1387674,
            timeZone = "America/New_York"
        )
    )

    private val repository = GeocodeRepository.Impl(googleApiClient)

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
}

private fun createMockEngine(latitude: Double, longitude: Double, timeZone: String) = MockEngine { httpRequestData ->
    val body = when (val path = httpRequestData.url.encodedPath) {
        "place/autocomplete/json" ->
            """{ "predictions" : [{ "place_id" : "ChIJZeH1eyl344kRA3v52Jl3kHo" }], "status" : "OK" }"""
        "geocode/json" ->
            """{ "results" : [{ "geometry" : { "location" : { "lat" : $latitude, "lng" : $longitude }}} ], "status" : "OK" }"""
        "timezone/json" ->
            """{ "dstOffset" : 3600, "rawOffset" : -18000, "status" : "OK", "timeZoneId" : "$timeZone", "timeZoneName" : "Eastern Daylight Time" }"""
        else -> fail("Invalid path $path")
    }
    respond(
        content = body,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}

