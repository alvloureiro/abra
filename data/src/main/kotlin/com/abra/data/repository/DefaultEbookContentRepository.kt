package com.abra.data.repository

import com.abra.data.local.dao.EbookDao
import com.abra.data.local.dao.ListeningSegmentDao
import com.abra.data.local.mapper.toDomain
import com.abra.domain.model.PdfExtractionResult
import com.abra.domain.repository.EbookContentRepository
import com.abra.domain.repository.PdfTextExtractor
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class DefaultEbookContentRepository
    @Inject
    constructor(
        private val ebookDao: EbookDao,
        private val listeningSegmentDao: ListeningSegmentDao,
        private val pdfTextExtractor: PdfTextExtractor,
        private val extractionResultApplier: EbookExtractionResultApplier,
    ) : EbookContentRepository {
    override fun observeSegments(ebookId: String) =
        listeningSegmentDao.observeSegments(ebookId).map { segments ->
            segments.map { it.toDomain() }
        }

    override suspend fun applyExtractionResult(
        ebookId: String,
        result: PdfExtractionResult,
        fallbackPageCount: Int,
    ): PdfExtractionResult = extractionResultApplier.apply(ebookId, result, fallbackPageCount)

    override suspend fun refreshExtractedText(ebookId: String): PdfExtractionResult {
        val ebook =
            ebookDao.getEbook(ebookId)
                ?: return PdfExtractionResult.Failure("Ebook was not found.")

        val result = pdfTextExtractor.extract(ebookId, ebook.sourceUri)
        return applyExtractionResult(
            ebookId = ebookId,
            result = result,
            fallbackPageCount = ebook.pageCount,
        )
    }
}
