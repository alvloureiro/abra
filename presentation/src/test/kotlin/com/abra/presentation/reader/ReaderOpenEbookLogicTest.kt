package com.abra.presentation.reader

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReaderOpenEbookLogicTest {
    @Test
    fun doesNotSetLoadingWhenSameEbookAlreadyLoaded() {
        assertFalse(
            shouldSetLoadingWhenOpeningEbook(
                currentSelectedEbookId = "ebook-1",
                loadedEbookId = "ebook-1",
                ebookId = "ebook-1",
            ),
        )
    }

    @Test
    fun setsLoadingWhenOpeningNewEbook() {
        assertTrue(
            shouldSetLoadingWhenOpeningEbook(
                currentSelectedEbookId = "ebook-1",
                loadedEbookId = "ebook-1",
                ebookId = "ebook-2",
            ),
        )
    }

    @Test
    fun setsLoadingWhenEbookNotYetLoaded() {
        assertTrue(
            shouldSetLoadingWhenOpeningEbook(
                currentSelectedEbookId = "ebook-1",
                loadedEbookId = null,
                ebookId = "ebook-1",
            ),
        )
    }
}
