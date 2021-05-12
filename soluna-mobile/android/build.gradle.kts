plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Versions.Android.compileSdk)

    defaultConfig {
        applicationId = "com.russhwolf.soluna.android"
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.AndroidX.compose
    }
}

dependencies {
    implementation(project(":soluna-mobile:shared"))

    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.AndroidX.Compose.ui)
    implementation(Deps.AndroidX.Compose.material)
    implementation(Deps.AndroidX.Compose.materialIcons)
    implementation(Deps.AndroidX.Compose.materialIconsExtended)
    implementation(Deps.AndroidX.Compose.tooling)
    implementation(Deps.AndroidX.lifecycleRuntime)
    implementation(Deps.AndroidX.activityCompose)
    implementation(Deps.AndroidX.navigationCompose)

    implementation(Deps.KotlinX.Coroutines.core)
    implementation(Deps.KotlinX.Coroutines.android)

    implementation(Deps.KotlinX.dateTime)
    coreLibraryDesugaring(Deps.desugar)

    implementation(Deps.Koin.core)
    implementation(Deps.Koin.compose)

    implementation(Deps.playServicesLocation)
    implementation(Deps.KotlinX.Coroutines.playServices)

    testImplementation(Deps.junit)
    androidTestImplementation(Deps.AndroidX.Test.junit)
    androidTestImplementation(Deps.AndroidX.Compose.test)

    // TODO remove this once compose transitively pulls this version (o/w transitive pull from jcenter)
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.4")
}
