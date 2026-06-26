package com.abra.data.local.mapper

import com.abra.data.local.entity.EbookEntity
import com.abra.data.local.entity.ListeningProgressEntity
import com.abra.data.local.entity.ListeningSegmentEntity
import com.abra.data.local.relation.EbookWithProgress
import com.abra.domain.model.Ebook
import com.abra.domain.model.EbookExtractionStatus
import com.abra.domain.model.EbookMetadata
import com.abra.domain.model.ListeningProgress
import com.abra.domain.model.ListeningSegment

fun EbookWithProgress.toDomain(): Ebook {
    return ebook.toDomain(progress?.toDomain())
}

fun EbookEntity.toDomain(progress: ListeningProgress? = null): Ebook {
    return Ebook(
        id = id,
        sourceUri = sourceUri,
        metadata = EbookMetadata(
            title = title,
            fileName = fileName,
            pageCount = pageCount
        ),
        importedAtEpochMillis = importedAtEpochMillis,
        extractionStatus = EbookExtractionStatus.valueOf(extractionStatus),
        extractionMessage = extractionMessage,
        progress = progress
    )
}

fun ListeningProgress.toEntity(): ListeningProgressEntity {
    return ListeningProgressEntity(
        ebookId = ebookId,
        segmentIndex = segmentIndex,
        characterOffset = characterOffset,
        completed = completed,
        updatedAtEpochMillis = updatedAtEpochMillis
    )
}

fun ListeningProgressEntity.toDomain(): ListeningProgress {
    return ListeningProgress(
        ebookId = ebookId,
        segmentIndex = segmentIndex,
        characterOffset = characterOffset,
        completed = completed,
        updatedAtEpochMillis = updatedAtEpochMillis
    )
}

fun ListeningSegment.toEntity(): ListeningSegmentEntity {
    return ListeningSegmentEntity(
        ebookId = ebookId,
        segmentIndex = index,
        pageNumber = pageNumber,
        text = text
    )
}

fun ListeningSegmentEntity.toDomain(): ListeningSegment {
    return ListeningSegment(
        ebookId = ebookId,
        index = segmentIndex,
        pageNumber = pageNumber,
        text = text
    )
}
