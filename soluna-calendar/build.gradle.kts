plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("com.russhwolf.soluna.calendar.MainKt")
}

tasks.withType<JavaExec> {
    args = (findProperty("args") as? String ?: "").split(" ")
}

dependencies {
    implementation(projects.solunaCore.islandTime)
    implementation(libs.islandTime)
    testImplementation(libs.junit)
    implementation(libs.kotlin.test.junit)
}

