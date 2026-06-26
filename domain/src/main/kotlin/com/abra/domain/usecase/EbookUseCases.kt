package com.abra.domain.usecase

import com.abra.domain.model.Ebook
import com.abra.domain.model.ListeningSegment
import com.abra.domain.repository.EbookContentRepository
import com.abra.domain.repository.EbookRepository
import com.abra.domain.repository.PdfTextExtractor
import kotlinx.coroutines.flow.Flow

class ImportEbookUseCase(
    private val ebookRepository: EbookRepository,
    private val pdfTextExtractor: PdfTextExtractor,
    private val ebookContentRepository: EbookContentRepository,
) {
    suspend operator fun invoke(sourceUri: String): Ebook {
        val pending = ebookRepository.createPendingImport(sourceUri)
        val result = pdfTextExtractor.extract(pending.id, sourceUri)
        ebookContentRepository.applyExtractionResult(pending.id, result)
        return ebookRepository.requireEbook(pending.id)
    }
}

class RefreshEbookTextUseCase(
    private val ebookContentRepository: EbookContentRepository,
) {
    suspend operator fun invoke(ebookId: String) =
        ebookContentRepository.refreshExtractedText(ebookId)
}

class GetEbooksUseCase(
    private val ebookRepository: EbookRepository,
) {
    operator fun invoke(): Flow<List<Ebook>> = ebookRepository.observeLibrary()
}

class GetEbookUseCase(
    private val ebookRepository: EbookRepository,
) {
    operator fun invoke(ebookId: String): Flow<Ebook?> = ebookRepository.observeEbook(ebookId)
}

class GetEbookTextUseCase(
    private val ebookContentRepository: EbookContentRepository,
) {
    operator fun invoke(ebookId: String): Flow<List<ListeningSegment>> =
        ebookContentRepository.observeSegments(ebookId)
}
