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
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":soluna-core-time"))
    implementation("io.islandtime:core:0.2.4")
    testImplementation("junit:junit:4.13")
    testImplementation(kotlin("test-junit"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
