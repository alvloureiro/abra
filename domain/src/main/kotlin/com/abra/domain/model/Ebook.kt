package com.abra.domain.model

data class Ebook(
    val id: String,
    val sourceUri: String,
    val metadata: EbookMetadata,
    val importedAtEpochMillis: Long,
    val extractionStatus: EbookExtractionStatus,
    val extractionMessage: String? = null,
    val progress: ListeningProgress? = null
)

data class EbookMetadata(
    val title: String,
    val fileName: String,
    val pageCount: Int
)

enum class EbookExtractionStatus {
    PENDING,
    READY,
    UNSUPPORTED,
    FAILED
}
