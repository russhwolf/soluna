object Versions {
    const val kotlin = "1.5.10"

    object Android {
        const val gradle = "7.0.0-beta03"
        const val minSdk = 23
        const val compileSdk = 30
        const val targetSdk = 30
    }

    const val buildKonfig = "0.8.0"
    const val desugar = "1.1.5"
    const val islandTime = "0.6.0"
    const val junit = "4.13.2"
    const val koin = "3.1.1"
    const val ktor = "1.6.0"
    const val playServicesLocation = "18.0.0"
    const val robolectric = "4.5.1"
    const val settings = "0.7.7"
    const val sqlDelight = "1.5.0"
    const val stately = "1.1.7"
    const val turbine = "0.5.2"

    object KotlinX {
        const val coroutines = "1.5.0-native-mt"
        const val dateTime = "0.2.1"
        const val serialization = "1.2.1"
    }

    object AndroidX {
        const val activityCompose = "1.3.0-beta01"
        const val compose = "1.0.0-beta08"
        const val coreKtx = "1.5.0"
        const val dataStore = "1.0.0-beta01"
        const val lifecycle = "2.4.0-alpha01"
        const val navigationCompose = "2.4.0-alpha02"
        const val workManager = "2.5.0"

        object Test {
            const val core = "1.3.0"
            const val junit = "1.1.2"
        }
    }
}

object Deps {
    const val desugar = "com.android.tools:desugar_jdk_libs:${Versions.desugar}"
    const val islandTime = "io.islandtime:core:${Versions.islandTime}"
    const val junit = "junit:junit:${Versions.junit}"
    const val playServicesLocation = "com.google.android.gms:play-services-location:${Versions.playServicesLocation}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"

    object AndroidX {
        const val activityCompose = "androidx.activity:activity-compose:${Versions.AndroidX.activityCompose}"
        const val coreKtx = "androidx.core:core-ktx:${Versions.AndroidX.coreKtx}"
        const val dataStore = "androidx.datastore:datastore-preferences:${Versions.AndroidX.dataStore}"
        const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.AndroidX.lifecycle}"
        const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.AndroidX.navigationCompose}"
        const val workManager = "androidx.work:work-runtime-ktx:${Versions.AndroidX.workManager}"

        object Compose {
            const val material = "androidx.compose.material:material:${Versions.AndroidX.compose}"
            const val materialIcons = "androidx.compose.material:material-icons-core:${Versions.AndroidX.compose}"
            const val materialIconsExtended =
                "androidx.compose.material:material-icons-extended:${Versions.AndroidX.compose}"
            const val test = "androidx.compose.ui:ui-test-junit4:${Versions.AndroidX.compose}"
            const val tooling = "androidx.compose.ui:ui-tooling:${Versions.AndroidX.compose}"
            const val ui = "androidx.compose.ui:ui:${Versions.AndroidX.compose}"
        }

        object Test {
            const val core = "androidx.test:core:${Versions.AndroidX.Test.core}"
            const val junit = "androidx.test.ext:junit:${Versions.AndroidX.Test.junit}"
        }
    }

    object KotlinX {
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.KotlinX.dateTime}"

        object Coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KotlinX.coroutines}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.KotlinX.coroutines}"
            const val playServices =
                "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.KotlinX.coroutines}"
        }

        object Serialization {
            const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.KotlinX.serialization}"
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KotlinX.serialization}"
        }
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koin}"
        const val workManager = "io.insert-koin:koin-androidx-workmanager:${Versions.koin}"
    }

    object Ktor {
        const val android = "io.ktor:ktor-client-android:${Versions.ktor}"
        const val core = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val ios = "io.ktor:ktor-client-ios:${Versions.ktor}"
        const val json = "io.ktor:ktor-client-json:${Versions.ktor}"
        const val logging = "io.ktor:ktor-client-logging:${Versions.ktor}"
        const val mock = "io.ktor:ktor-client-mock:${Versions.ktor}"
        const val serialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"
    }

    object SqlDelight {
        const val android = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
        const val coroutines = "com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
        const val jvm = "com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}"
        const val native = "com.squareup.sqldelight:native-driver:${Versions.sqlDelight}"
    }

    object Settings {
        const val core = "com.russhwolf:multiplatform-settings:${Versions.settings}"
        const val coroutines = "com.russhwolf:multiplatform-settings-coroutines:${Versions.settings}"
        const val dataStore = "com.russhwolf:multiplatform-settings-datastore:${Versions.settings}"
        const val test = "com.russhwolf:multiplatform-settings-test:${Versions.settings}"
    }

    object Stately {
        const val core = "co.touchlab:stately-common:${Versions.stately}"
    }
}
