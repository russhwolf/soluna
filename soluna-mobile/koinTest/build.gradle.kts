import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework.BitcodeEmbeddingMode.BITCODE
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

val koinVersion = "3.0.1-alpha-2"

kotlin {
    android()
    val isDevice = System.getenv("SDK_NAME")?.startsWith("iphoneos") == true
    val ios: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = if (isDevice) ::iosArm64 else ::iosX64
    ios("ios") {
        binaries {
            framework {
                baseName = "KoinTest"
                embedBitcode = BITCODE
                transitiveExport = true
            }
        }
        compilations["main"].kotlinOptions.freeCompilerArgs += listOf("-linker-options", "-lsqlite3")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":soluna-mobile:shared"))

                implementation("org.koin:koin-core:$koinVersion")

                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation("org.koin:koin-test:$koinVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))

                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))

                implementation("androidx.test:core:1.2.0")
                implementation("androidx.test.ext:junit:1.1.1")
                implementation("org.robolectric:robolectric:4.3.1")
            }
        }
        val iosMain by getting {
            dependencies {
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(15)

        javaCompileOptions.annotationProcessorOptions.includeCompileClasspath = true
    }

    testOptions.unitTests.isIncludeAndroidResources = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lintOptions {
        // Because of the mix between test and main code, lint sees some things it doesn't like.
        // This is fine here because we're only ever using this in tests.
        disable("InvalidPackage")
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

tasks.create<Exec>("xcodeTest") {
    group = "verification"
    dependsOn("linkDebugTestIos")
    workingDir("$projectDir/../ios/")
    commandLine(
        "xcodebuild",
        "test",
        "-scheme", "Soluna",
        "-destination", "platform=iOS Simulator,name=iPhone 11",
        "-only-testing:SolunaTests/KoinModulesTestSwift"
    )
}
tasks["allTests"].dependsOn("xcodeTest")
