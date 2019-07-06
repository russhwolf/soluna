pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.android")) {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}
rootProject.name = "Soluna"

enableFeaturePreview("GRADLE_METADATA")

include(":soluna-core", ":soluna-calendar", ":soluna-mobile", ":soluna-mobile:shared")
