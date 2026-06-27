package com.abra.presentation.reader

internal fun shouldSetLoadingWhenOpeningEbook(
    currentSelectedEbookId: String?,
    loadedEbookId: String?,
    ebookId: String,
): Boolean = !(currentSelectedEbookId == ebookId && loadedEbookId == ebookId)
