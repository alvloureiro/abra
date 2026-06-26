plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.abra"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.abra"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    lint {
        abortOnError = true
        checkDependencies = true
        disable += setOf("AndroidGradlePluginVersion", "GradleDependency")
        warningsAsErrors = true
        htmlReport = true
        xmlReport = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.hilt.android)
    implementation(libs.room.runtime)

    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata.jvm)

    debugImplementation(libs.compose.ui.tooling)

    runtimeOnly(libs.coroutines.android)
}

ktlint {
    android.set(true)
    ignoreFailures.set(false)
    outputToConsole.set(true)
    filter {
        exclude { entry -> entry.file.path.contains("/generated/") }
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    parallel = true
}
