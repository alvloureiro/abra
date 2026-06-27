plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.abra.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = true
        checkDependencies = true
        disable += setOf("GradleDependency", "AndroidGradlePluginVersion")
        warningsAsErrors = true
        htmlReport = true
        xmlReport = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

hilt {
    enableAggregatingTask = true
}

ksp {
    arg("room.incremental", "true")
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    api(project(":domain"))

    implementation(libs.androidx.core.ktx)
    api(libs.coroutines.core)
    api(libs.datastore.core)
    api(libs.datastore.preferences.core)
    api(libs.hilt.android)
    api(libs.javax.inject)
    implementation(libs.pdfbox.android) {
        exclude(group = "org.bouncycastle", module = "bcpkix-jdk15to18")
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15to18")
        exclude(group = "org.bouncycastle", module = "bcutil-jdk15to18")
    }
    api(libs.room.runtime)

    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata.jvm)
    ksp(libs.room.compiler)

    testImplementation(libs.coroutines.test)
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
