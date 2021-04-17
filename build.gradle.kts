import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version Versions.kotlin apply false
    id("com.android.library") version Versions.Android.gradle apply false
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.withType<AbstractTestTask> {
        testLogging {
            showStandardStreams = true
            events("passed", "failed")
        }
    }
}
