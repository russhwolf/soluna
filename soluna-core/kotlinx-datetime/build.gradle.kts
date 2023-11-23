plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":soluna-core"))
                api(libs.kotlinx.dateTime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

