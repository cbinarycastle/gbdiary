plugins {
    id("java-platform")
    id("maven-publish")
}

val coreKtx = "1.8.0"
val appcompat = "1.4.2"
val material = "1.6.1"
val constraintLayout = "2.1.4"
val junit = "4.13.2"
val junitExt = "1.1.3"
val espresso = "3.4.0"

dependencies {
    constraints {
        api("${Libs.CORE_KTX}:$coreKtx")
        api("${Libs.APPCOMPAT}:$appcompat")
        api("${Libs.MATERIAL}:$material")
        api("${Libs.CONSTRAINT_LAYOUT}:$constraintLayout")
        api("${Libs.JUNIT}:$junit")
        api("${Libs.JUNIT_EXT}:$junitExt")
        api("${Libs.ESPRESSO}:$espresso")
    }
}

publishing {
    publications {
        create<MavenPublication>("myPlatform") {
            from(components["javaPlatform"])
        }
    }
}
