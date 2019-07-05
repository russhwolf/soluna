pluginManagement {
    repositories {
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}
rootProject.name = "Soluna"

enableFeaturePreview("GRADLE_METADATA")

include(":soluna-core", ":soluna-calendar")
