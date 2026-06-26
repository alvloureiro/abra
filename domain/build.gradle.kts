plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(libs.coroutines.core)

    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
}

ktlint {
    ignoreFailures.set(false)
    outputToConsole.set(true)
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    parallel = true
}
