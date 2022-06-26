plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    api(platform(project(":depconstraints")))
    androidTestApi(platform(project(":depconstraints")))

    implementation(Libs.CORE_KTX)
    implementation(Libs.ACTIVITY_KTX)
    implementation(Libs.APPCOMPAT)
    implementation(Libs.MATERIAL)
    implementation(Libs.CONSTRAINT_LAYOUT)

    implementation(Libs.COROUTINES)

    implementation(platform(Libs.FIREBASE_BOM))
    implementation(Libs.FIREBASE_AUTH)

    implementation(Libs.GOOGLE_PLAY_SERVICES_AUTH)

    implementation(Libs.TIMBER)

    testImplementation(Libs.JUNIT)
    androidTestImplementation(Libs.JUNIT_EXT)
    androidTestImplementation(Libs.ESPRESSO)
}