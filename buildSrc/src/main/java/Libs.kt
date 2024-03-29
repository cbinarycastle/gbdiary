object Libs {

    object Accompanist {
        private const val VERSION = "0.28.0"
        const val PAGER = "com.google.accompanist:accompanist-pager:$VERSION"
        const val PERMISSIONS = "com.google.accompanist:accompanist-permissions:$VERSION"
        const val SYSTEM_UI_CONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:$VERSION"
    }

    object Android {
        const val BILLING = "com.android.billingclient:billing-ktx:5.0.0"
        const val DESUGARING = "com.android.tools:desugar_jdk_libs:1.1.5"
        const val MATERIAL = "com.google.android.material:material:1.6.1"
    }

    object AndroidX {
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.4.2"
        const val BIOMETRIC = "androidx.biometric:biometric:1.1.0"
        const val CORE_KTX = "androidx.core:core-ktx:1.8.0"
        const val DATASTORE_PREFERENCES = "androidx.datastore:datastore-preferences:1.0.0"
        const val STARTUP = "androidx.startup:startup-runtime:1.1.1"

        object Activity {
            const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:1.5.0"
            const val ACTIVITY_KTX = "androidx.activity:activity-ktx:1.4.0"
        }

        object Compose {
            private const val VERSION = "1.3.1"
            const val FOUNDATION = "androidx.compose.foundation:foundation:$VERSION"
            const val MATERIAL = "androidx.compose.material:material:$VERSION"
            const val PREVIEW = "androidx.compose.ui:ui-tooling-preview:$VERSION"
            const val RUNTIME = "androidx.compose.runtime:runtime:$VERSION"
            const val UI = "androidx.compose.ui:ui:$VERSION"
            const val UI_TOOLING = "androidx.compose.ui:ui-tooling:$VERSION"
            const val UI_UTIL = "androidx.compose.ui:ui-util:$VERSION"
        }

        object Hilt {
            const val NAVIGATION_COMPOSE = "androidx.hilt:hilt-navigation-compose:1.0.0"
        }

        object Lifecycle {
            const val VIEWMODEL_COMPOSE = "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.0"
        }

        object Navigation {
            const val COMPOSE = "androidx.navigation:navigation-compose:2.5.3"
        }

        object Room {
            private const val VERSION = "2.4.2"
            const val COMPILER = "androidx.room:room-compiler:$VERSION"
            const val ROOM_KTX = "androidx.room:room-ktx:$VERSION"
            const val RUNTIME = "androidx.room:room-runtime:$VERSION"
        }

        object Test {
            const val ESPRESSO = "androidx.test.espresso:espresso-core:3.4.0"
            const val JUNIT_EXT = "androidx.test.ext:junit:1.1.3"
        }
    }

    object AppLovin {
        const val APPLOVIN = "com.applovin:applovin-sdk:11.4.5"
    }

    object Coil {
        const val COMPOSE = "io.coil-kt:coil-compose:2.1.0"
    }

    object Coroutines {
        private const val VERSION = "1.6.4"
        const val ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$VERSION"
        const val TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$VERSION"
    }

    object Firebase {
        const val BOM = "com.google.firebase:firebase-bom:30.3.2"
        const val ANALYTICS = "com.google.firebase:firebase-analytics-ktx"
        const val CRASHLYTICS = "com.google.firebase:firebase-crashlytics-ktx"
    }

    object GoogleDrive {
        const val API_CLIENT = "com.google.api-client:google-api-client-android:2.0.0"
        const val DRIVE = "com.google.apis:google-api-services-drive:v3-rev197-1.25.0"
        const val HTTP_CLIENT = "com.google.http-client:google-http-client-gson:1.42.2"
    }

    object GooglePlayCore {
        const val REVIEW = "com.google.android.play:review-ktx:2.0.0"
    }

    object GooglePlayServices {
        const val AUTH = "com.google.android.gms:play-services-auth:20.2.0"
        const val ADS_IDENTIFIER = "com.google.android.gms:play-services-ads-identifier:18.0.1"
    }

    object Hilt {
        private const val VERSION = "2.42"
        const val ANDROID = "com.google.dagger:hilt-android:$VERSION"
        const val COMPILER = "com.google.dagger:hilt-android-compiler:$VERSION"
    }

    object Kotlin {
        const val TEST_JUNIT = "org.jetbrains.kotlin:kotlin-test-junit:1.7.10"
    }

    object Mockito {
        const val CORE = "org.mockito:mockito-core:1.10.19"
    }

    object Timber {
        const val TIMBER = "com.jakewharton.timber:timber:5.0.1"
    }
}

object Plugins {
    const val ANDROID_GRADLE_PLUGIN = "com.android.tools.build:gradle:7.2.1"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase:firebase-crashlytics-gradle:2.9.1"
    const val GOOGLE_SERVICES = "com.google.gms:google-services:4.3.13"
    const val HILT_AGP = "com.google.dagger:hilt-android-gradle-plugin:2.42"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0"
}