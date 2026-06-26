package com.abra.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abra.domain.usecase.GetEbooksUseCase
import com.abra.domain.usecase.ImportEbookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val getEbooks: GetEbooksUseCase,
        private val importEbook: ImportEbookUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LibraryUiState())
        val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

        init {
            viewModelScope.launch {
                getEbooks()
                    .catch { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Unable to load library.",
                            )
                        }
                    }.collect { ebooks ->
                        _uiState.update {
                            it.copy(
                                ebooks = ebooks,
                                isLoading = false,
                                errorMessage = null,
                            )
                        }
                    }
            }
        }

        fun importPdf(sourceUri: String) {
            viewModelScope.launch {
                _uiState.update { it.copy(isImporting = true, errorMessage = null) }
                runCatching { importEbook(sourceUri) }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(errorMessage = error.message ?: "Unable to import ebook.")
                        }
                    }
                _uiState.update { it.copy(isImporting = false) }
            }
        }
    }
