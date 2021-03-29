plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    iosArm64()
    iosX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":soluna-core"))
                api(Deps.KotlinX.dateTime)
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

