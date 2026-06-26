import org.gradle.api.artifacts.ProjectDependency

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dependency.check)
}

dependencyCheck {
    failBuildOnCVSS = 7.0F
    formats = listOf("HTML", "JSON")
    analyzers.assemblyEnabled = false
    suppressionFile = "$rootDir/config/dependency-check/suppressions.xml"
    providers.environmentVariable("NVD_API_KEY").orNull?.let { apiKey ->
        nvd.apiKey = apiKey
    }
}

ktlint {
    ignoreFailures.set(false)
    outputToConsole.set(true)
    filter {
        exclude { entry -> entry.file.path.contains("/generated/") }
    }
}

dependencyAnalysis {
    issues {
        all {
            onUnusedDependencies {
                severity("fail")
            }
            onUnusedAnnotationProcessors {
                severity("fail")
            }
            onRuntimeOnly {
                severity("fail")
            }
            onCompileOnly {
                severity("fail")
            }
            onDuplicateClassWarnings {
                severity("fail")
            }
            onUsedTransitiveDependencies {
                severity("warn")
            }
            onIncorrectConfiguration {
                severity("warn")
            }
        }
    }
}

tasks.register("validateModuleBoundaries") {
    group = "verification"
    description = "Validates clean architecture module dependencies."

    doLast {
        val forbiddenDependencies =
            mapOf(
                ":domain" to setOf(":data", ":presentation", ":app"),
                ":data" to setOf(":presentation", ":app"),
                ":presentation" to setOf(":data", ":app"),
            )

        forbiddenDependencies.forEach { (modulePath, forbiddenPaths) ->
            val moduleProject = project(modulePath)
            val projectDependencies =
                moduleProject.configurations
                    .flatMap { configuration -> configuration.dependencies }
                    .filterIsInstance<ProjectDependency>()
                    .map { dependency -> dependency.path }
                    .toSet()

            val violations = projectDependencies.intersect(forbiddenPaths)
            check(violations.isEmpty()) {
                "$modulePath has forbidden dependencies: ${violations.joinToString()}"
            }
        }
    }
}

tasks.register("staticAnalysis") {
    group = "verification"
    description = "Runs Kotlin style, code smell, and Android lint checks."
    dependsOn(
        "ktlintCheck",
        ":app:detekt",
        ":data:detekt",
        ":domain:detekt",
        ":presentation:detekt",
        ":app:lintDebug",
        ":data:lintDebug",
        ":presentation:lintDebug",
    )
}

tasks.register("dependencyHealth") {
    group = "verification"
    description = "Runs dependency declaration and vulnerability checks."
    dependsOn(
        "buildHealth",
        "dependencyCheckAggregate",
    )
}

tasks.register("installQualityHooks") {
    group = "verification"
    description = "Installs local check-only Git hooks for Kotlin style."
    dependsOn("addKtlintCheckGitPreCommitHook")
}

tasks.register("qualityGate") {
    group = "verification"
    description = "Runs the fail-fast code health gate."
    dependsOn(
        "validateModuleBoundaries",
        "staticAnalysis",
        "dependencyHealth",
        ":domain:test",
        ":data:testDebugUnitTest",
        ":presentation:testDebugUnitTest",
        ":app:assembleDebug",
    )
}
