import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.russhwolf.soluna.calendar.MainKt")
}

tasks.withType<JavaExec> {
    args = (findProperty("args") as? String ?: "").split(" ")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

dependencies {
    implementation(project(":soluna-core:island-time"))
    implementation(libs.islandTime)
    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit"))
}

