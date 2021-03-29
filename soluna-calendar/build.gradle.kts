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
    implementation(Deps.islandTime)
    testImplementation(Deps.junit)
    testImplementation(kotlin("test-junit"))
}

