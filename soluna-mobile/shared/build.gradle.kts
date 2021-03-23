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
    id("kotlinx-serialization") version "1.4.31"
    id("com.squareup.sqldelight") version "1.4.4"
    id("com.codingfeline.buildkonfig") version "0.7.0"
}

val coroutineVersion = "1.4.2-native-mt"
val ktorVersion = "1.5.0"
val koinVersion = "3.0.0-alpha-4"
val sqldelightVersion = "1.4.4"
val serializationVersion = "1.0.1"
val settingsVersion = "0.7.4"
val statelyVersion = "1.1.1"
val turbineVersion = "0.4.0"

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
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsApi")
                useExperimentalAnnotation("com.russhwolf.settings.ExperimentalSettingsImplementation")
                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            }
        }

        commonMain {
            dependencies {
                implementation(project(":soluna-core:kotlinx-datetime"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core") {
                    version {
                        strictly(coroutineVersion)
                    }
                }

                implementation("com.squareup.sqldelight:coroutines-extensions:$sqldelightVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")

                implementation("co.touchlab:stately-common:$statelyVersion")

                implementation("org.koin:koin-core:$koinVersion")

                implementation("com.russhwolf:multiplatform-settings:$settingsVersion")
                implementation("com.russhwolf:multiplatform-settings-coroutines-native-mt:$settingsVersion")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation("io.ktor:ktor-client-mock:$ktorVersion")

                implementation("org.koin:koin-test:$koinVersion")

                implementation("com.russhwolf:multiplatform-settings-test:$settingsVersion")

                implementation("app.cash.turbine:turbine:$turbineVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android") {
                    version {
                        strictly(coroutineVersion)
                    }
                }

                implementation("com.squareup.sqldelight:android-driver:$sqldelightVersion")

                implementation("io.ktor:ktor-client-android:$ktorVersion")

                implementation("androidx.datastore:datastore-preferences:1.0.0-alpha08")
                implementation("com.russhwolf:multiplatform-settings-datastore:$settingsVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                implementation("androidx.test:core:1.3.0")
                implementation("androidx.test.ext:junit:1.1.2")
                implementation("org.robolectric:robolectric:4.5.1")

                implementation("com.squareup.sqldelight:sqlite-driver:$sqldelightVersion")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:$sqldelightVersion")

                implementation("io.ktor:ktor-client-ios:$ktorVersion")
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
