package com.abra.domain.usecase

import com.abra.domain.model.Ebook
import com.abra.domain.model.ListeningSegment
import com.abra.domain.repository.EbookContentRepository
import com.abra.domain.repository.EbookRepository
import kotlinx.coroutines.flow.Flow

class ImportEbookUseCase(
    private val ebookRepository: EbookRepository
) {
    suspend operator fun invoke(sourceUri: String): Ebook {
        return ebookRepository.importEbook(sourceUri)
    }
}

class GetEbooksUseCase(
    private val ebookRepository: EbookRepository
) {
    operator fun invoke(): Flow<List<Ebook>> {
        return ebookRepository.observeLibrary()
    }
}

class GetEbookUseCase(
    private val ebookRepository: EbookRepository
) {
    operator fun invoke(ebookId: String): Flow<Ebook?> {
        return ebookRepository.observeEbook(ebookId)
    }
}

class GetEbookTextUseCase(
    private val ebookContentRepository: EbookContentRepository
) {
    operator fun invoke(ebookId: String): Flow<List<ListeningSegment>> {
        return ebookContentRepository.observeSegments(ebookId)
    }
}
