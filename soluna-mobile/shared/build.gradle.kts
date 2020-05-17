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
    id("kotlinx-serialization") version "1.3.72"
    id("com.squareup.sqldelight") version "1.3.0"
    id("com.codingfeline.buildkonfig") version "0.5.1"
}

val coroutineVersion = "1.3.5"
val coroutineWorkerVersion = "0.5.0"
val ktorVersion = "1.3.2"
val koinVersion = "3.0.0-alpha-2"
val sqldelightVersion = "1.3.0"
val serializationVersion = "0.20.0"
val statelyVersion = "1.0.2"

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
        compilations["main"].kotlinOptions.freeCompilerArgs += "-Xobjc-generics"
    }

    sourceSets {
        all {
            languageSettings.apply {
                progressiveMode = true
                useExperimentalAnnotation("kotlin.Experimental")
                useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }

        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":soluna-core"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutineVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serializationVersion")

                implementation("io.ktor:ktor-client-core-native:$ktorVersion")
                implementation("io.ktor:ktor-client-json-native:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-native:$ktorVersion")
                implementation("io.ktor:ktor-client-logging-native:$ktorVersion")

                implementation("com.autodesk:coroutineworker:$coroutineWorkerVersion")
                implementation("co.touchlab:stately-common:$statelyVersion")
                implementation("co.touchlab:stately-concurrency:$statelyVersion")

                implementation("org.koin:koin-core:$koinVersion")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(project(":soluna-mobile:koinTest"))

                implementation("io.ktor:ktor-client-mock-native:$ktorVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("com.squareup.sqldelight:android-driver:$sqldelightVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")

                implementation("io.ktor:ktor-client-android:$ktorVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                implementation("androidx.test:core:1.2.0")
                implementation("androidx.test.ext:junit:1.1.1")
                implementation("org.robolectric:robolectric:4.3.1")

                implementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
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
    compileSdkVersion(28)
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
