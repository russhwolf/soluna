plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("maven-publish")
}
repositories {
    mavenCentral()
}
group = "com.example"
version = "0.0.1"

kotlin {
    jvm()
    iosArm64()
    iosX64()
    sourceSets {
        all {
            languageSettings.apply {
                enableLanguageFeature("InlineClasses")
                progressiveMode = true
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
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
                implementation(kotlin("stdlib"))
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
