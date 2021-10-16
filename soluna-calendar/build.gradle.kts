plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "com.russhwolf.soluna.calendar.MainKt"
}

tasks.withType(JavaExec::class) {
    args = (findProperty("args") as? String ?: "").split(" ")
}

dependencies {
    implementation(project(":soluna-core:island-time"))
    implementation(libs.islandTime)
    testImplementation(libs.junit)
    testImplementation(kotlin("test-junit"))
}

