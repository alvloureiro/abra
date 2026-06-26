import org.gradle.api.artifacts.ProjectDependency

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}

tasks.register("validateModuleBoundaries") {
    group = "verification"
    description = "Validates clean architecture module dependencies."

    doLast {
        val forbiddenDependencies = mapOf(
            ":domain" to setOf(":data", ":presentation", ":app"),
            ":data" to setOf(":presentation", ":app"),
            ":presentation" to setOf(":data", ":app")
        )

        forbiddenDependencies.forEach { (modulePath, forbiddenPaths) ->
            val moduleProject = project(modulePath)
            val projectDependencies = moduleProject.configurations
                .flatMap { configuration -> configuration.dependencies }
                .filterIsInstance<ProjectDependency>()
                .map { dependency -> dependency.dependencyProject.path }
                .toSet()

            val violations = projectDependencies.intersect(forbiddenPaths)
            check(violations.isEmpty()) {
                "$modulePath has forbidden dependencies: ${violations.joinToString()}"
            }
        }
    }
}

tasks.register("qualityGate") {
    group = "verification"
    description = "Runs the MVP baseline quality gate."
    dependsOn(
        "validateModuleBoundaries",
        ":domain:test",
        ":data:testDebugUnitTest",
        ":presentation:testDebugUnitTest",
        ":app:lintDebug",
        ":app:assembleDebug"
    )
}
