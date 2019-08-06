package com.russhwolf.soluna.mobile.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.Ios

actual val httpClientEngine: HttpClientEngine = Ios.create()
