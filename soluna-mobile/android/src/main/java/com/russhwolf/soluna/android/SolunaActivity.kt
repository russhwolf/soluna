package com.russhwolf.soluna.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.russhwolf.soluna.android.ui.SolunaApp

class SolunaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SolunaApp()
        }
    }
}
