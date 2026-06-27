package com.abra.presentation.settings

internal data class OpenSourceLibrary(
    val name: String,
    val licenseName: String,
    val url: String,
)

internal const val PRIVACY_POLICY_URL =
    "https://github.com/alvloureiro/abra/blob/main/docs/privacy-policy.md"

internal const val SOURCE_REPOSITORY_URL = "https://github.com/alvloureiro/abra"

internal val openSourceLibraries =
    listOf(
        OpenSourceLibrary(
            name = "Android Jetpack (Compose, Room, DataStore, Lifecycle)",
            licenseName = "Apache License 2.0",
            url = "https://github.com/androidx/androidx",
        ),
        OpenSourceLibrary(
            name = "Dagger Hilt",
            licenseName = "Apache License 2.0",
            url = "https://github.com/google/dagger",
        ),
        OpenSourceLibrary(
            name = "Kotlin Coroutines",
            licenseName = "Apache License 2.0",
            url = "https://github.com/Kotlin/kotlinx.coroutines",
        ),
        OpenSourceLibrary(
            name = "PDFBox Android",
            licenseName = "Apache License 2.0",
            url = "https://github.com/TomRoush/PdfBox-Android",
        ),
    )
