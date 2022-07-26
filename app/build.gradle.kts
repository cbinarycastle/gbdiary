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
    implementation(Libs.AndroidX.Activity.ACTIVITY_COMPOSE)
    implementation(Libs.AndroidX.Hilt.NAVIGATION_COMPOSE)
    implementation(Libs.AndroidX.Lifecycle.VIEWMODEL_COMPOSE)

    implementation(Libs.Coroutines.ANDROID)

    implementation(Libs.GooglePlayServices.AUTH)

    implementation("com.google.http-client:google-http-client-gson:1.26.0")
    implementation("com.google.api-client:google-api-client-android:1.26.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    implementation(Libs.Hilt.ANDROID)
    kapt(Libs.Hilt.COMPILER)

    testImplementation(Libs.JUnit.JUNIT)
    androidTestImplementation(Libs.AndroidX.Test.ESPRESSO)
    androidTestImplementation(Libs.AndroidX.Test.JUNIT_EXT)

    implementation(Libs.Timber.TIMBER)
}