import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "0.3.0-build138"
    application
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

application {
    mainClass.set("com.russhwolf.soluna.calendar.MainKt")
}

tasks.withType(JavaExec::class) {
    args = (findProperty("args") as? String ?: "").split(" ")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":soluna-core-time"))
    implementation("io.islandtime:core:0.4.0")
    testImplementation("junit:junit:4.13.1")
    testImplementation(kotlin("test-junit"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
}

//compose.desktop {
//    application {
//        mainClass = "com.russhwolf.soluna.calendar.MainKt"
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = "soluna"
//        }
//    }
//}
