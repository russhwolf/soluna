object Versions {
    const val kotlin = "1.4.32"
    const val android = "4.0.2"

    const val buildKonfig = "0.7.0"
    const val islandTime = "0.5.0"
    const val junit = "4.13.2"
    const val koin = "3.0.1-beta-2"
    const val ktor = "1.5.3"
    const val robolectric = "4.5.1"
    const val settings = "0.7.4"
    const val sqlDelight = "1.4.4"
    const val stately = "1.1.5"
    const val turbine = "0.4.1"

    object KotlinX {
        const val coroutines = "1.4.3-native-mt"
        const val dateTime = "0.1.1"
        const val serialization = "1.1.0"
    }

    object AndroidX {
        const val dataStore = "1.0.0-alpha08"

        object Test {
            const val core = "1.3.0"
            const val junit = "1.1.2"
        }
    }
}

object Deps {
    const val islandTime = "io.islandtime:core:${Versions.islandTime}"
    const val junit = "junit:junit:${Versions.junit}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"

    object AndroidX {
        const val dataStore = "androidx.datastore:datastore-preferences:${Versions.AndroidX.dataStore}"

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
        }

        object Serialization {
            const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.KotlinX.serialization}"
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KotlinX.serialization}"
        }
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
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
        const val coroutines = "com.russhwolf:multiplatform-settings-coroutines-native-mt:${Versions.settings}"
        const val dataStore = "com.russhwolf:multiplatform-settings-datastore:${Versions.settings}"
        const val test = "com.russhwolf:multiplatform-settings-test:${Versions.settings}"
    }

    object Stately {
        const val core = "co.touchlab:stately-common:${Versions.stately}"
    }
}
