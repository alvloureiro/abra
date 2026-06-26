package com.abra.data.repository

import com.abra.data.local.dao.EbookDao
import com.abra.data.local.dao.ListeningSegmentDao
import com.abra.data.local.mapper.toDomain
import com.abra.data.local.mapper.toEntity
import com.abra.domain.model.EbookExtractionStatus
import com.abra.domain.model.PdfExtractionResult
import com.abra.domain.repository.EbookContentRepository
import com.abra.domain.repository.PdfTextExtractor
import kotlinx.coroutines.flow.map

class DefaultEbookContentRepository(
    private val ebookDao: EbookDao,
    private val listeningSegmentDao: ListeningSegmentDao,
    private val pdfTextExtractor: PdfTextExtractor,
) : EbookContentRepository {
    override fun observeSegments(ebookId: String) =
        listeningSegmentDao.observeSegments(ebookId).map { segments ->
            segments.map { it.toDomain() }
        }

    override suspend fun refreshExtractedText(ebookId: String): PdfExtractionResult {
        val ebook =
            ebookDao.getEbook(ebookId)
                ?: return PdfExtractionResult.Failure("Ebook was not found.")

        return when (val result = pdfTextExtractor.extract(ebookId, ebook.sourceUri)) {
            is PdfExtractionResult.Success -> {
                listeningSegmentDao.replaceSegments(
                    ebookId = ebookId,
                    segments = result.segments.map { it.toEntity() },
                )
                ebookDao.updateExtraction(
                    ebookId = ebookId,
                    status = EbookExtractionStatus.READY.name,
                    message = null,
                    pageCount = result.pageCount,
                )
                result
            }

            is PdfExtractionResult.Unsupported -> {
                ebookDao.updateExtraction(
                    ebookId = ebookId,
                    status = EbookExtractionStatus.UNSUPPORTED.name,
                    message = result.reason,
                    pageCount = result.pageCount,
                )
                result
            }

            is PdfExtractionResult.Failure -> {
                ebookDao.updateExtraction(
                    ebookId = ebookId,
                    status = EbookExtractionStatus.FAILED.name,
                    message = result.message,
                    pageCount = ebook.pageCount,
                )
                result
            }
        }
    }
}
