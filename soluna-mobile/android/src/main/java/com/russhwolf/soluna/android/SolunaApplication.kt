package com.russhwolf.soluna.android

import android.app.Application
import android.content.Context
import com.russhwolf.soluna.mobile.initKoin
import org.koin.dsl.module

class SolunaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(module {
            single<Context> { applicationContext }
        })
    }
}