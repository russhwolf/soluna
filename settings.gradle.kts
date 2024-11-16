rootProject.name = "Soluna"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":soluna-core")
include(":soluna-core:island-time")
include(":soluna-core:kotlinx-datetime")
include(":soluna-calendar-core")
include(":soluna-calendar-core:island-time")
include(":soluna-calendar-core:kotlinx-datetime")
include(":soluna-calendar")
include(":soluna-compose-calendar")
include(":soluna-mobile")
include(":soluna-mobile:shared")
include(":soluna-mobile:android")
include(":soluna-mobile-new")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
