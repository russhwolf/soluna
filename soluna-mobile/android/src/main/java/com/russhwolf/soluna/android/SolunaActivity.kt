package com.russhwolf.soluna.android

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.russhwolf.soluna.android.ui.SolunaApp
import com.russhwolf.soluna.mobile.initKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class SolunaActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin(module {
            single<Context> { applicationContext }
            single<ComponentActivity> { this@SolunaActivity }
        })

        setContent {
            SolunaApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }
}
