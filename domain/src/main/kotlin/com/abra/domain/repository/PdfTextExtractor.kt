package com.abra.domain.repository

import com.abra.domain.model.PdfExtractionResult

interface PdfTextExtractor {
    suspend fun extract(
        ebookId: String,
        sourceUri: String,
    ): PdfExtractionResult
}
