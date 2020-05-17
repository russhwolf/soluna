pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        jcenter()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.library",
                "com.android.application" -> useModule("com.android.tools.build:gradle:${requested.version}")
                "com.squareup.sqldelight" -> useModule("com.squareup.sqldelight:gradle-plugin:${requested.version}")
                "kotlinx-serialization" -> useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
                "com.codingfeline.buildkonfig" -> useModule("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:${requested.version}")
            }
        }
    }
}
rootProject.name = "Soluna"

enableFeaturePreview("GRADLE_METADATA")

include(":soluna-core")
include(":soluna-calendar")
include(":soluna-mobile")
include(":soluna-mobile:shared")
include(":soluna-mobile:koinTest")
