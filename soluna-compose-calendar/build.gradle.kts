plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.solunaCalendarCore)
                implementation(projects.solunaCore)
                implementation(projects.solunaCalendarCore.kotlinxDatetime)
                implementation(projects.solunaCore.kotlinxDatetime)
                implementation(libs.kotlinx.dateTime)

                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
            }
        }
    }
}
