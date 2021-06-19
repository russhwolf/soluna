plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

kotlin {
    jvm()
    iosArm64()
    iosX64()
    sourceSets {
        all {
            languageSettings.apply {
                progressiveMode = true
            }
        }

        val commonMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}
