package com.russhwolf.soluna.mobile.api

import com.russhwolf.soluna.mobile.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.http.encodeURLQueryComponent
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

interface GoogleApiClient {
    suspend fun getPlaceAutocomplete(query: String): PlaceAutocompleteResponse

    suspend fun getGeocode(placeId: String): GeocodeResponse

    suspend fun getTimeZone(latitude: Double, longitude: Double, timestamp: Long): TimeZoneResponse

    class Impl(httpClientEngine: HttpClientEngine) : GoogleApiClient {
        @UseExperimental(UnstableDefault::class)
        private val httpClient = HttpClient(httpClientEngine) {
            defaultRequest {
                url.protocol = URLProtocol.HTTPS
                url.host = "maps.googleapis.com/maps/api"
                parameter("key", BuildKonfig.GOOGLE_API_KEY)
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json.nonstrict).apply {
                    setMapper(PlaceAutocompleteResponse::class, PlaceAutocompleteResponse.serializer())
                    setMapper(GeocodeResponse::class, GeocodeResponse.serializer())
                    setMapper(TimeZoneResponse::class, TimeZoneResponse.serializer())
                }
            }
            install(Logging)
        }

        override suspend fun getPlaceAutocomplete(query: String): PlaceAutocompleteResponse =
            httpClient.getWithTimeout {
                url {
                    encodedPath = "place/autocomplete/json"
                    parameter("input", query.encodeURLQueryComponent(spaceToPlus = true))
                    parameter("types", "geocode")
                }
            }

        override suspend fun getGeocode(placeId: String): GeocodeResponse =
            httpClient.getWithTimeout {
                url {
                    encodedPath = "geocode/json"
                    parameter("place_id", placeId)
                }
            }

        override suspend fun getTimeZone(latitude: Double, longitude: Double, timestamp: Long): TimeZoneResponse =
            httpClient.getWithTimeout {
                url {
                    encodedPath = "timezone/json"
                    parameter("location", "$latitude,$longitude")
                    parameter("timestamp", timestamp)
                }
            }
    }
}

@Serializable
data class PlaceAutocompleteResponse(val status: String, val predictions: List<Prediction>) {

    @Serializable
    data class Prediction(val place_id: String)
}

@Serializable
data class GeocodeResponse(val status: String, val results: List<Result>) {

    @Serializable
    data class Result(val geometry: Geometry) {

        @Serializable
        data class Geometry(val location: Location) {

            @Serializable
            data class Location(val lat: Double, val lng: Double)
        }
    }
}

@Serializable
data class TimeZoneResponse(val status: String, val rawOffset: Int, val dstOffset: Int, val timeZoneId: String)

private suspend inline fun <reified T> HttpClient.getWithTimeout(
    timeout: Long = 10_000,
    crossinline block: HttpRequestBuilder.() -> Unit
): T = withTimeout(timeout) { get<T>(block = block) }
