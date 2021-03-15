import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

application {
    mainClassName = "com.russhwolf.soluna.calendar.MainKt"
}

tasks.withType(JavaExec::class) {
    args = (findProperty("args") as? String ?: "").split(" ")
}

dependencies {
    implementation(project(":soluna-core:island-time"))
    implementation("io.islandtime:core:0.4.0")
    testImplementation("junit:junit:4.13.1")
    testImplementation(kotlin("test-junit"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
