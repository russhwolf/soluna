import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.util.*

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries {
            framework {
                baseName = "Shared"
                export(libs.kotlinx.dateTime)
            }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                progressiveMode = true
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("com.russhwolf.settings.ExperimentalSettingsApi")
                optIn("com.russhwolf.settings.ExperimentalSettingsImplementation")
                optIn("kotlin.time.ExperimentalTime")
            }
        }

        matching { it.name.endsWith("Test") }.configureEach {
            languageSettings.apply {
                optIn("kotlinx.coroutines.FlowPreview")
            }
        }

        commonMain {
            dependencies {
                implementation(projects.solunaCore.kotlinxDatetime)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.sqlDelight.coroutines)
                implementation(libs.bundles.kotlinx.serialization.common)
                implementation(libs.bundles.ktor.client.common)
                api(libs.kotlinx.dateTime)
                implementation(libs.koin.core)
                implementation(libs.bundles.settings.common)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(libs.ktor.client.mock)
                implementation(libs.koin.test)
                implementation(libs.settings.test)
                implementation(libs.turbine)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)

                implementation(libs.sqlDelight.android)

                implementation(libs.ktor.client.android)

                implementation(libs.koin.android)

                implementation(libs.androidx.dataStore)
                implementation(libs.settings.dataStore)

                implementation(libs.androidx.activityCompose)
                implementation(libs.playServicesLocation)
                implementation(libs.kotlinx.coroutines.playServices)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.test.junit)
                implementation(libs.robolectric)

                implementation(libs.sqlDelight.jvm)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.sqlDelight.native)
                implementation(libs.ktor.client.ios)
            }
        }
        iosTest {
            dependencies {
            }
        }
    }
}

buildkonfig {
    packageName = "com.russhwolf.soluna.mobile"

    // TODO move this somewhere more central (buildSrc?)
    val localProperties: Properties by lazy {
        Properties().apply {
            val localPropertiesFile = project.rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                load(localPropertiesFile.inputStream())
            }
        }
    }

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "GOOGLE_API_KEY", localProperties.getProperty("googleApiKey") ?: "")
    }
}

sqldelight {
    databases.create("SolunaDb") {
        packageName.set("com.russhwolf.soluna.mobile.db")
    }
}

android {
    namespace = "com.russhwolf.soluna.mobile.shared"

    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    testOptions.unitTests.isIncludeAndroidResources = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
