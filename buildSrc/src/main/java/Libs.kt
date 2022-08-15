object Libs {

    object Accompanist {
        private const val VERSION = "0.25.0"
        const val PAGER = "com.google.accompanist:accompanist-pager:$VERSION"
        const val SYSTEM_UI_CONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:$VERSION"
    }

    object Android {
        const val BILLING = "com.android.billingclient:billing-ktx:5.0.0"
        const val MATERIAL = "com.google.android.material:material:1.6.1"
    }

    object AndroidX {
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.4.2"
        const val CORE_KTX = "androidx.core:core-ktx:1.8.0"

        object Activity {
            const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:1.5.0"
            const val ACTIVITY_KTX = "androidx.activity:activity-ktx:1.4.0"
        }

        object Compose {
            private const val VERSION = "1.2.1"
            const val MATERIAL = "androidx.compose.material:material:$VERSION"
            const val PREVIEW = "androidx.compose.ui:ui-tooling-preview:$VERSION"
            const val RUNTIME = "androidx.compose.runtime:runtime:$VERSION"
            const val UI = "androidx.compose.ui:ui:$VERSION"
            const val UI_TOOLING = "androidx.compose.ui:ui-tooling:$VERSION"
        }

        object Hilt {
            const val NAVIGATION_COMPOSE = "androidx.hilt:hilt-navigation-compose:1.0.0"
        }

        object Lifecycle {
            const val VIEWMODEL_COMPOSE = "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.0"
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

    object Coil {
        const val COMPOSE = "io.coil-kt:coil-compose:2.1.0"
    }

    object Coroutines {
        private const val VERSION = "1.6.4"
        const val ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$VERSION"
        const val TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$VERSION"
    }

    object GooglePlayServices {
        const val AUTH = "com.google.android.gms:play-services-auth:20.2.0"
    }

    object GoogleDrive {
        const val API_CLIENT = "com.google.api-client:google-api-client-android:2.0.0"
        const val DRIVE = "com.google.apis:google-api-services-drive:v3-rev197-1.25.0"
        const val HTTP_CLIENT = "com.google.http-client:google-http-client-gson:1.42.2"
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

    object ThreeTen {
        const val THREE_TEN = "com.jakewharton.threetenabp:threetenabp:1.4.0"
    }

    object Timber {
        const val TIMBER = "com.jakewharton.timber:timber:5.0.1"
    }
}

object Plugins {
    const val ANDROID_GRADLE_PLUGIN = "com.android.tools.build:gradle:7.2.1"
    const val GOOGLE_SERVICES = "com.google.gms:google-services:4.3.12"
    const val HILT_AGP = "com.google.dagger:hilt-android-gradle-plugin:2.42"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0"
}