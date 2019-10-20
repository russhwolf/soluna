import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework.BitcodeEmbeddingMode.BITCODE
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization") version "1.3.50"
    id("com.squareup.sqldelight") version "1.2.0"
    id("org.jetbrains.kotlin.xcode-compat") version "0.2.3"
    id("com.codingfeline.buildkonfig") version "0.3.3"
}

val coroutineVersion = "1.3.0"
val coroutineWorkerVersion = "0.3.0"
val ktorVersion = "1.2.4"
val sqldelightVersion = "1.2.0"
val serializationVersion = "0.12.0"
val statelyVersion = "0.9.3"

kotlin {
    android()
    xcode {
        setupFramework("ios") {
            baseName = "Shared"
            embedBitcode = BITCODE
            transitiveExport = true
        }
    }
    (targets["ios"] as KotlinNativeTarget).compilations["main"].extraOpts.add("-Xobjc-generics")

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

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutineVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")

                implementation("com.autodesk:coroutineworker:$coroutineWorkerVersion")
                implementation("co.touchlab:stately:$statelyVersion")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation("io.ktor:ktor-client-mock:$ktorVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("com.squareup.sqldelight:android-driver:$sqldelightVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")

                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                implementation("androidx.test:core:1.2.0")
                implementation("androidx.test.ext:junit:1.1.1")
                implementation("org.robolectric:robolectric:4.3")

                implementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")

                implementation("com.squareup.sqldelight:sqlite-driver:$sqldelightVersion")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:ios-driver:$sqldelightVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutineVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$serializationVersion")

                implementation("io.ktor:ktor-client-ios:$ktorVersion")
                implementation("io.ktor:ktor-client-core-native:$ktorVersion")
                implementation("io.ktor:ktor-client-json-native:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-native:$ktorVersion")
                implementation("io.ktor:ktor-client-logging-native:$ktorVersion")
            }
        }
        val iosTest by getting {
            dependencies {
                implementation("io.ktor:ktor-client-mock-native:$ktorVersion")
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

tasks.create("iosTest") {
    dependsOn("linkDebugTestIos")
    doLast {
        val testBinaryPath =
            (kotlin.targets["ios"] as KotlinNativeTarget).binaries.getTest("DEBUG").outputFile.absolutePath
        exec {
            commandLine("xcrun", "simctl", "spawn", "--standalone", "iPhone Xʀ", testBinaryPath)
        }
    }
}
tasks["check"].dependsOn("iosTest")
