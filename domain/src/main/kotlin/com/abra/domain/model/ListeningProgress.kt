package com.abra.domain.model

data class ListeningProgress(
    val ebookId: String,
    val segmentIndex: Int,
    val characterOffset: Int,
    val completed: Boolean,
    val updatedAtEpochMillis: Long
) {
    fun percent(totalSegments: Int): Float {
        if (totalSegments <= 0) return 0f
        if (completed) return 1f
        return (segmentIndex + 1).coerceAtMost(totalSegments).toFloat() / totalSegments
    }
}

data class ListeningSegment(
    val ebookId: String,
    val index: Int,
    val pageNumber: Int,
    val text: String
)
