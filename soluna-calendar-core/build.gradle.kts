plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    sourceSets {
        all {
            languageSettings.apply {
                progressiveMode = true
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(projects.solunaCore)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
