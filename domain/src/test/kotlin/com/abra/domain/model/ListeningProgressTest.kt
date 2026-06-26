package com.abra.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ListeningProgressTest {
    @Test
    fun percentReturnsZeroWhenThereAreNoSegments() {
        val progress =
            ListeningProgress(
                ebookId = "ebook-1",
                segmentIndex = 0,
                characterOffset = 0,
                completed = false,
                updatedAtEpochMillis = 0L,
            )

        assertEquals(0f, progress.percent(totalSegments = 0))
    }

    @Test
    fun percentReturnsCompleteWhenProgressIsCompleted() {
        val progress =
            ListeningProgress(
                ebookId = "ebook-1",
                segmentIndex = 1,
                characterOffset = 0,
                completed = true,
                updatedAtEpochMillis = 0L,
            )

        assertEquals(1f, progress.percent(totalSegments = 10))
    }
}
