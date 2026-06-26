package com.abra.domain.repository

import com.abra.domain.model.ListeningSegment
import com.abra.domain.model.PdfExtractionResult
import kotlinx.coroutines.flow.Flow

interface EbookContentRepository {
    fun observeSegments(ebookId: String): Flow<List<ListeningSegment>>

    suspend fun applyExtractionResult(
        ebookId: String,
        result: PdfExtractionResult,
        fallbackPageCount: Int = 0,
    ): PdfExtractionResult

    suspend fun refreshExtractedText(ebookId: String): PdfExtractionResult
}
