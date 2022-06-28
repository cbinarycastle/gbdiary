plugins {
    id("java-platform")
    id("maven-publish")
}

val activityKtx = "1.4.0"
val appcompat = "1.4.2"
val constraintLayout = "2.1.4"
val coreKtx = "1.8.0"
val coroutines = "1.6.3"
val espresso = "3.4.0"
val firebaseBom = "30.1.0"
val googlePlayServicesAuth = "20.2.0"
val hilt = "2.42"
val junit = "4.13.2"
val junitExt = "1.1.3"
val lifecycleRuntimeKtx = "2.4.0"
val material = "1.6.1"
val timber = "5.0.1"

dependencies {
    constraints {
        api("${Libs.ACTIVITY_KTX}:$activityKtx")
        api("${Libs.APPCOMPAT}:$appcompat")
        api("${Libs.CONSTRAINT_LAYOUT}:$constraintLayout")
        api("${Libs.CORE_KTX}:$coreKtx")
        api("${Libs.COROUTINES}:$coroutines")
        api("${Libs.ESPRESSO}:$espresso")
        api("${Libs.FIREBASE_BOM}:$firebaseBom")
        api("${Libs.GOOGLE_PLAY_SERVICES_AUTH}:$googlePlayServicesAuth")
        api("${Libs.HILT_ANDROID}:$hilt")
        api("${Libs.HILT_COMPILER}:$hilt")
        api("${Libs.JUNIT}:$junit")
        api("${Libs.JUNIT_EXT}:$junitExt")
        api("${Libs.LIFECYCLE_RUNTIME_KTX}:$lifecycleRuntimeKtx")
        api("${Libs.MATERIAL}:$material")
        api("${Libs.TIMBER}:$timber")
    }
}

publishing {
    publications {
        create<MavenPublication>("myPlatform") {
            from(components["javaPlatform"])
        }
    }
}
