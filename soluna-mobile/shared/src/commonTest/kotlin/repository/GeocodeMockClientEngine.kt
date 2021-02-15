package com.russhwolf.soluna.mobile.repository

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

fun createGeocodeMockClientEngine(geocodeMap: Map<String, GeocodeData> = mutableMapOf()): MockEngine {
    val ids = geocodeMap.keys.mapIndexed { index, query ->
        query to "${index + 1}"
    }.toMap()

    val locations = geocodeMap.values.mapIndexed { index, geocodeData ->
        "${index + 1}" to (geocodeData.latitude to geocodeData.longitude)
    }.toMap()

    val timeZones = geocodeMap.values.associate { geocodeData ->
        "${geocodeData.latitude},${geocodeData.longitude}" to geocodeData.timeZone
    }

    fun getResponseBody(httpRequestData: HttpRequestData): String? {
        return when (httpRequestData.url.encodedPath) {
            "place/autocomplete/json" -> {
                val query = httpRequestData.url.parameters["input"]
                val id = ids[query] ?: return null
                """{ "predictions" : [{ "place_id" : "$id" }] }"""
            }
            "geocode/json" -> {
                val placeId = httpRequestData.url.parameters["place_id"]
                val location = locations[placeId] ?: return null
                """{ "results" : [{ "geometry" : { "location" : { "lat" : ${location.first}, "lng" : ${location.second} } } }] }"""
            }
            "timezone/json" -> {
                val location = httpRequestData.url.parameters["location"]
                val timeZone = timeZones[location] ?: return null
                """{ "timeZoneId" : "$timeZone" }"""
            }
            else -> null
        }
    }

    return MockEngine { httpRequestData ->
        val body = getResponseBody(httpRequestData)
        if (body != null) {
            respond(
                content = body,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        } else {
            respondError(HttpStatusCode.NotFound)
        }
    }
}
