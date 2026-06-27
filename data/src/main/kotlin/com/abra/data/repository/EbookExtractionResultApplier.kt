package com.abra.data.repository

import com.abra.data.local.dao.EbookDao
import com.abra.data.local.dao.ListeningSegmentDao
import com.abra.data.local.mapper.toEntity
import com.abra.domain.model.EbookExtractionStatus
import com.abra.domain.model.PdfExtractionResult
import javax.inject.Inject

class EbookExtractionResultApplier
    @Inject
    constructor(
        private val ebookDao: EbookDao,
        private val listeningSegmentDao: ListeningSegmentDao,
    ) {
    suspend fun apply(
        ebookId: String,
        result: PdfExtractionResult,
        fallbackPageCount: Int = 0,
    ): PdfExtractionResult {
        when (result) {
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
            }

            is PdfExtractionResult.Unsupported -> {
                ebookDao.updateExtraction(
                    ebookId = ebookId,
                    status = EbookExtractionStatus.UNSUPPORTED.name,
                    message = result.reason,
                    pageCount = result.pageCount,
                )
            }

            is PdfExtractionResult.Failure -> {
                ebookDao.updateExtraction(
                    ebookId = ebookId,
                    status = EbookExtractionStatus.FAILED.name,
                    message = result.message,
                    pageCount = fallbackPageCount,
                )
            }
        }
        return result
    }
}
