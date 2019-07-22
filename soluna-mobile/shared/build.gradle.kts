import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight") version "1.1.4"
}

val coroutineVersion = "1.2.2"
val sqldelightVersion = "1.1.4"

kotlin {
    android()
    val iosTarget = if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) {
        presets.getByName("iosArm64")
    } else {
        presets.getByName("iosX64")
    }
    targetFromPreset(iosTarget, "ios")

    sourceSets {
        all {
            languageSettings.apply {
                progressiveMode = true
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":soluna-core"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutineVersion")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("com.squareup.sqldelight:android-driver:$sqldelightVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                implementation("androidx.test:core:1.2.0")
                implementation("androidx.test.ext:junit:1.1.1")
                implementation("org.robolectric:robolectric:4.3")

                implementation("com.squareup.sqldelight:android-driver:$sqldelightVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:ios-driver:$sqldelightVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutineVersion")
            }
        }
        val iosTest by getting {
            dependencies {

            }
        }
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
            commandLine("xcrun", "simctl", "spawn", "iPhone Xʀ", testBinaryPath)
        }
    }
}
tasks["check"].dependsOn("iosTest")
