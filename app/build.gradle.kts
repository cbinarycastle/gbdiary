plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = "com.casoft.gbdiary"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = Versions.VERSION_CODE
        versionName = Versions.VERSION_NAME
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
            )
        )
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
    debugImplementation(Libs.AndroidX.Compose.UI_TOOLING)
    implementation(Libs.AndroidX.Compose.PREVIEW)
    implementation(Libs.AndroidX.Activity.ACTIVITY_COMPOSE)
    implementation(Libs.AndroidX.Hilt.NAVIGATION_COMPOSE)
    implementation(Libs.AndroidX.Lifecycle.VIEWMODEL_COMPOSE)
    implementation(Libs.Coil.COMPOSE)

    kapt(Libs.AndroidX.Room.COMPILER)
    implementation(Libs.AndroidX.Room.RUNTIME)
    implementation(Libs.AndroidX.Room.ROOM_KTX)

    implementation(Libs.Coroutines.ANDROID)
    testImplementation(Libs.Coroutines.TEST)

    implementation(Libs.Accompanist.PAGER)
    implementation(Libs.Accompanist.SYSTEM_UI_CONTROLLER)

    implementation(Libs.GooglePlayServices.AUTH)

    implementation(Libs.GoogleDrive.HTTP_CLIENT)
    implementation(Libs.GoogleDrive.API_CLIENT)
    implementation(Libs.GoogleDrive.DRIVE)

    implementation(Libs.Hilt.ANDROID)
    kapt(Libs.Hilt.COMPILER)

    androidTestImplementation(Libs.AndroidX.Test.ESPRESSO)
    androidTestImplementation(Libs.AndroidX.Test.JUNIT_EXT)
    testImplementation(Libs.Kotlin.TEST_JUNIT)
    testImplementation(Libs.Mockito.CORE)

    implementation(Libs.ThreeTen.THREE_TEN)

    implementation(Libs.Timber.TIMBER)
}