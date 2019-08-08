package com.russhwolf.soluna.mobile.api

import com.russhwolf.soluna.mobile.runBlockingTest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class GoogleApiClientTest {
    private val googleApiClient by lazy { GoogleApiClient.Impl(mockEngine) }

    @Test
    fun placeAutoComplete_success() = runBlockingTest {
        val response = googleApiClient.getPlaceAutocomplete("Somerville, MA")
        val expected = PlaceAutocompleteResponse(
            status = "OK",
            predictions = listOf(
                PlaceAutocompleteResponse.Prediction("ChIJZeH1eyl344kRA3v52Jl3kHo"),
                PlaceAutocompleteResponse.Prediction("ChIJ02moHil344kRROXnKRTRZgo")
            )
        )
        assertEquals(expected, response)
    }

    @Test
    fun geocode_success() = runBlockingTest {
        val response = googleApiClient.getGeocode("ChIJZeH1eyl344kRA3v52Jl3kHo")
        val expected = GeocodeResponse(
            status = "OK",
            results = listOf(
                GeocodeResponse.Result(
                    GeocodeResponse.Result.Geometry(
                        GeocodeResponse.Result.Geometry.Location(
                            lat = 42.3875968,
                            lng = -71.0994968
                        )
                    )
                )
            )
        )
        assertEquals(expected, response)
    }

    @Test
    fun timezone_success() = runBlockingTest {
        val response =
            googleApiClient.getTimeZone(latitude = 42.3875968, longitude = -71.0994968, timestamp = 1565055420)
        val expected = TimeZoneResponse(
            status = "OK",
            rawOffset = -18000,
            dstOffset = 3600,
            timeZoneId = "America/New_York"
        )
        assertEquals(expected, response)
    }
}

private val mockEngine = MockEngine { httpRequestData ->
    val body = when (val path = httpRequestData.url.encodedPath) {
        "place/autocomplete/json" ->
            """
            {
               "predictions" : [
                  {
                     "description" : "Somerville, Massachusetts, USA",
                     "id" : "872cf5f484d44c852949aabca7ae9c0bbc434b99",
                     "matched_substrings" : [
                        {
                           "length" : 10,
                           "offset" : 0
                        },
                        {
                           "length" : 2,
                           "offset" : 12
                        }
                     ],
                     "place_id" : "ChIJZeH1eyl344kRA3v52Jl3kHo",
                     "reference" : "ChIJZeH1eyl344kRA3v52Jl3kHo",
                     "structured_formatting" : {
                        "main_text" : "Somerville",
                        "main_text_matched_substrings" : [
                           {
                              "length" : 10,
                              "offset" : 0
                           }
                        ],
                        "secondary_text" : "Massachusetts, USA",
                        "secondary_text_matched_substrings" : [
                           {
                              "length" : 2,
                              "offset" : 0
                           }
                        ]
                     },
                     "terms" : [
                        {
                           "offset" : 0,
                           "value" : "Somerville"
                        },
                        {
                           "offset" : 12,
                           "value" : "Massachusetts"
                        },
                        {
                           "offset" : 27,
                           "value" : "USA"
                        }
                     ],
                     "types" : [ "locality", "political", "geocode" ]
                  },
                  {
                     "description" : "Magoun Square, Somerville, MA, USA",
                     "id" : "41e291d318fec7b7254355c136da72f5ab7afe54",
                     "matched_substrings" : [
                        {
                           "length" : 2,
                           "offset" : 0
                        },
                        {
                           "length" : 10,
                           "offset" : 15
                        }
                     ],
                     "place_id" : "ChIJ02moHil344kRROXnKRTRZgo",
                     "reference" : "ChIJ02moHil344kRROXnKRTRZgo",
                     "structured_formatting" : {
                        "main_text" : "Magoun Square",
                        "main_text_matched_substrings" : [
                           {
                              "length" : 2,
                              "offset" : 0
                           }
                        ],
                        "secondary_text" : "Somerville, MA, USA",
                        "secondary_text_matched_substrings" : [
                           {
                              "length" : 10,
                              "offset" : 0
                           }
                        ]
                     },
                     "terms" : [
                        {
                           "offset" : 0,
                           "value" : "Magoun Square"
                        },
                        {
                           "offset" : 15,
                           "value" : "Somerville"
                        },
                        {
                           "offset" : 27,
                           "value" : "MA"
                        },
                        {
                           "offset" : 31,
                           "value" : "USA"
                        }
                     ],
                     "types" : [ "neighborhood", "political", "geocode" ]
                  }
               ],
               "status" : "OK"
            }
            """.trimIndent()
        "geocode/json" ->
            """
            {
               "results" : [
                  {
                     "address_components" : [
                        {
                           "long_name" : "Somerville",
                           "short_name" : "Somerville",
                           "types" : [ "locality", "political" ]
                        },
                        {
                           "long_name" : "Middlesex County",
                           "short_name" : "Middlesex County",
                           "types" : [ "administrative_area_level_2", "political" ]
                        },
                        {
                           "long_name" : "Massachusetts",
                           "short_name" : "MA",
                           "types" : [ "administrative_area_level_1", "political" ]
                        },
                        {
                           "long_name" : "United States",
                           "short_name" : "US",
                           "types" : [ "country", "political" ]
                        }
                     ],
                     "formatted_address" : "Somerville, MA, USA",
                     "geometry" : {
                        "bounds" : {
                           "northeast" : {
                              "lat" : 42.418118,
                              "lng" : -71.0729949
                           },
                           "southwest" : {
                              "lat" : 42.37296600000001,
                              "lng" : -71.13450089999999
                           }
                        },
                        "location" : {
                           "lat" : 42.3875968,
                           "lng" : -71.0994968
                        },
                        "location_type" : "APPROXIMATE",
                        "viewport" : {
                           "northeast" : {
                              "lat" : 42.418118,
                              "lng" : -71.0729949
                           },
                           "southwest" : {
                              "lat" : 42.37296600000001,
                              "lng" : -71.13450089999999
                           }
                        }
                     },
                     "place_id" : "ChIJZeH1eyl344kRA3v52Jl3kHo",
                     "types" : [ "locality", "political" ]
                  }
               ],
               "status" : "OK"
            }
            """.trimIndent()
        "timezone/json" ->
            """
            {
               "dstOffset" : 3600,
               "rawOffset" : -18000,
               "status" : "OK",
               "timeZoneId" : "America/New_York",
               "timeZoneName" : "Eastern Daylight Time"
            }
            """.trimIndent()
        else -> fail("Invalid path $path")
    }
    respond(
        content = body,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}
