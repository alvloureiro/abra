package com.abra.domain.model

sealed interface PdfExtractionResult {
    data class Success(
        val segments: List<ListeningSegment>,
        val pageCount: Int
    ) : PdfExtractionResult

    data class Unsupported(
        val reason: String,
        val pageCount: Int = 0
    ) : PdfExtractionResult

    data class Failure(
        val message: String
    ) : PdfExtractionResult
}
