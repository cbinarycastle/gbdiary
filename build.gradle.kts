// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("${Plugins.ANDROID_GRADLE_PLUGIN}:${Versions.ANDROID_GRADLE_PLUGIN}")
        classpath("${Plugins.KOTLIN}:${Versions.KOTLIN}")
        classpath("${Plugins.GOOGLE_SERVICES}:${Versions.GOOGLE_SERVICES}")
        classpath("${Plugins.HILT_AGP}:${Versions.HILT_AGP}")
    }
}