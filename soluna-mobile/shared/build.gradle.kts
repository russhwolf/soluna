import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework.BitcodeEmbeddingMode.BITCODE
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization") version Versions.kotlin
    id("com.squareup.sqldelight") version Versions.sqlDelight
    id("com.codingfeline.buildkonfig") version Versions.buildKonfig
}

kotlin {
    android()
    val isDevice = System.getenv("SDK_NAME")?.startsWith("iphoneos") == true
    val ios: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = if (isDevice) ::iosArm64 else ::iosX64
    ios("ios") {
        binaries {
            framework {
                baseName = "Shared"
                embedBitcode = BITCODE
                transitiveExport = true
            }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                progressiveMode = true
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsApi")
                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsImplementation")
                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            }
        }

        commonMain {
            dependencies {
                implementation(project(":soluna-core:kotlinx-datetime"))

                implementation(Deps.KotlinX.Coroutines.core) {
                    version {
                        strictly(Versions.KotlinX.coroutines)
                    }
                }

                implementation(Deps.SqlDelight.coroutines)

                implementation(Deps.KotlinX.Serialization.core)
                implementation(Deps.KotlinX.Serialization.json)

                implementation(Deps.Ktor.core)
                implementation(Deps.Ktor.json)
                implementation(Deps.Ktor.serialization)
                implementation(Deps.Ktor.logging)

                implementation(Deps.KotlinX.dateTime)

                implementation(Deps.Stately.core)

                implementation(Deps.Koin.core)

                implementation(Deps.Settings.core)
                implementation(Deps.Settings.coroutines)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation(Deps.Ktor.mock)

                implementation(Deps.Koin.test)

                implementation(Deps.Settings.test)

                implementation(Deps.turbine)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Deps.KotlinX.Coroutines.android) {
                    version {
                        strictly(Versions.KotlinX.coroutines)
                    }
                }

                implementation(Deps.SqlDelight.android)

                implementation(Deps.Ktor.android)

                implementation(Deps.AndroidX.dataStore)
                implementation(Deps.Settings.dataStore)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                implementation(Deps.AndroidX.Test.core)
                implementation(Deps.AndroidX.Test.junit)
                implementation(Deps.robolectric)

                implementation(Deps.SqlDelight.jvm)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Deps.SqlDelight.native)

                implementation(Deps.Ktor.ios)
            }
        }
        val iosTest by getting {
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sqldelight {
    database("SolunaDb") {
        packageName = "com.russhwolf.soluna.mobile.db"
    }
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(15)
    }

    testOptions.unitTests.isIncludeAndroidResources = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// Adapted from Jetbrains xcode-compat plugin
kotlin.targets.withType<KotlinNativeTarget>().configureEach {
    binaries.withType<Framework>().configureEach {
        val buildType = NativeBuildType.valueOf(
            System.getenv("CONFIGURATION")?.toUpperCase()
                ?: "DEBUG"
        )
        if (this.buildType == buildType) {
            var dsymTask: Sync? = null

            if (buildType == NativeBuildType.DEBUG) {
                dsymTask = project.task<Sync>("buildForXcodeDSYM") {
                    dependsOn(linkTask)
                    val outputFile = linkTask.outputFile.get()
                    val outputDSYM = File(outputFile.parent, outputFile.name + ".dSYM")
                    from(outputDSYM)
                    into(File(System.getenv("CONFIGURATION_BUILD_DIR"), outputDSYM.name))
                }
            }

            val buildForXcodeTask = project.task<Sync>("buildForXcode") {
                dependsOn(dsymTask ?: linkTask)
                val outputFile = linkTask.outputFile.get()
                from(outputFile)
                into(File(System.getenv("CONFIGURATION_BUILD_DIR"), outputFile.name))
            }
        }
    }
}
