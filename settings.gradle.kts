pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-metadata-jvm:2.4.0")
    }
}

plugins {
    // Build-health analyzes Android/Kotlin projects from the settings classloader.
    id("com.autonomousapps.build-health") version "3.16.0"
    id("com.android.application") version "9.0.1" apply false
    id("com.android.library") version "9.0.1" apply false
    id("com.google.devtools.ksp") version "2.3.9" apply false
    id("com.google.dagger.hilt.android") version "2.60" apply false
    id("org.jetbrains.kotlin.jvm") version "2.4.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0" apply false
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Abra"
include(":app")
include(":data")
include(":domain")
include(":presentation")
