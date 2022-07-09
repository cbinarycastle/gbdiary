plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = "io.github.cbinarycastle.diary"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = Versions.VERSION_CODE
        versionName = Versions.VERSION_NAME
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(Libs.Android.BILLING)
    implementation(Libs.Android.MATERIAL)

    implementation(Libs.AndroidX.APPCOMPAT)
    implementation(Libs.AndroidX.CORE_KTX)
    implementation(Libs.AndroidX.Activity.ACTIVITY_KTX)

    implementation(Libs.AndroidX.Compose.MATERIAL)
    implementation(Libs.AndroidX.Compose.RUNTIME)
    implementation(Libs.AndroidX.Compose.UI)
    implementation(Libs.AndroidX.Activity.ACTIVITY_COMPOSE)
    implementation(Libs.AndroidX.Hilt.NAVIGATION_COMPOSE)
    implementation(Libs.AndroidX.Lifecycle.VIEWMODEL_COMPOSE)

    implementation(Libs.Coroutines.ANDROID)

    implementation(platform(Libs.Firebase.BOM))
    implementation(Libs.Firebase.AUTH_KTX)
    implementation(Libs.GooglePlayServices.AUTH)

    implementation(Libs.Hilt.ANDROID)
    kapt(Libs.Hilt.COMPILER)

    testImplementation(Libs.JUnit.JUNIT)
    androidTestImplementation(Libs.AndroidX.Test.ESPRESSO)
    androidTestImplementation(Libs.AndroidX.Test.JUNIT_EXT)

    implementation(Libs.Timber.TIMBER)
}