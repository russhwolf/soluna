plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.solunaCalendarCore)
                implementation(projects.solunaCore.kotlinxDatetime)
                implementation(libs.kotlinx.dateTime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

