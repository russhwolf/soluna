package com.russhwolf.soluna.mobile.api

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

actual val httpClientEngine: HttpClientEngine = Android.create()
