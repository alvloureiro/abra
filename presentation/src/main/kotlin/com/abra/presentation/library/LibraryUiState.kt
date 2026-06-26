package com.abra.presentation.library

import com.abra.domain.model.Ebook

data class LibraryUiState(
    val ebooks: List<Ebook> = emptyList(),
    val isLoading: Boolean = true,
    val isImporting: Boolean = false,
    val errorMessage: String? = null,
)
