plugins {
    kotlin("multiplatform").version("1.3.50").apply(false)
    id("com.android.library").version("3.4.1").apply(false)
}

allprojects {
    configurations.create("compileClasspath")
}
