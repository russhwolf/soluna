package com.russhwolf.soluna.mobile.api

import com.russhwolf.soluna.mobile.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.http.encodeURLQueryComponent
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface GoogleApiClient {
    suspend fun getPlaceAutocomplete(query: String): PlaceAutocompleteResponse?

    suspend fun getGeocode(placeId: String): GeocodeResponse?

    suspend fun getTimeZone(latitude: Double, longitude: Double, timestamp: Long): TimeZoneResponse?

    class Impl(httpClientEngine: HttpClientEngine, logger: Logger) : GoogleApiClient {
        private val httpClient = HttpClient(httpClientEngine) {
            expectSuccess = true
            defaultRequest {
                url.protocol = URLProtocol.HTTPS
                url.host = "maps.googleapis.com/maps/api"
                url.parameters["key"] = BuildKonfig.GOOGLE_API_KEY
            }
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    allowSpecialFloatingPointValues = true
                    useAlternativeNames = false
                })
            }
            install(Logging) {
                this.logger = logger
                level = LogLevel.ALL
            }
        }

        override suspend fun getPlaceAutocomplete(query: String): PlaceAutocompleteResponse? =
            httpClient.getBodyWithTimeoutOrNull {
                url {
                    encodedPath = "place/autocomplete/json"
                    parameter("input", query.encodeURLQueryComponent(spaceToPlus = true))
                    parameter("types", "geocode")
                }
            }

        override suspend fun getGeocode(placeId: String): GeocodeResponse? =
            httpClient.getBodyWithTimeoutOrNull {
                url {
                    encodedPath = "geocode/json"
                    parameter("place_id", placeId)
                }
            }

        override suspend fun getTimeZone(latitude: Double, longitude: Double, timestamp: Long): TimeZoneResponse? =
            httpClient.getBodyWithTimeoutOrNull {
                url {
                    encodedPath = "timezone/json"
                    parameter("location", "$latitude,$longitude")
                    parameter("timestamp", timestamp)
                }
            }
    }
}

@Serializable
data class PlaceAutocompleteResponse(
    val status: String? = null,
    val predictions: List<Prediction>? = null
) {

    @Serializable
    data class Prediction(
        val place_id: String? = null
    )
}

@Serializable
data class GeocodeResponse(
    val status: String? = null,
    val results: List<Result>? = null
) {

    @Serializable
    data class Result(
        val geometry: Geometry? = null
    ) {

        @Serializable
        data class Geometry(
            val location: Location? = null
        ) {

            @Serializable
            data class Location(
                val lat: Double? = null,
                val lng: Double? = null
            )
        }
    }
}

@Serializable
data class TimeZoneResponse(
    val status: String? = null,
    val rawOffset: Int? = null,
    val dstOffset: Int? = null,
    val timeZoneId: String? = null
)

private suspend inline fun <reified T> HttpClient.getBodyWithTimeoutOrNull(
    timeout: Long = 10_000,
    crossinline block: HttpRequestBuilder.() -> Unit
): T? =
    try {
        withTimeout(timeout) { get(block = block).body<T>() }
    } catch (exception: TimeoutCancellationException) {
        null
    } catch (exception: ClientRequestException) {
        null
    }
