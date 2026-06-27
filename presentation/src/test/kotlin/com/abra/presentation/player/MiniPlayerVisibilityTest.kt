package com.abra.presentation.player

import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.PlaybackStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MiniPlayerVisibilityTest {
    @Test
    fun showsMiniPlayerOnLibraryWhenPlaying() {
        val playback =
            AudioPlaybackState(
                ebookId = "ebook-1",
                status = PlaybackStatus.PLAYING,
                segmentIndex = 2,
                totalSegments = 10,
            )

        assertTrue(
            shouldShowMiniPlayerBar(
                playbackState = playback,
                onListenTab = false,
                selectedEbookId = "ebook-1",
            ),
        )
    }

    @Test
    fun hidesMiniPlayerOnListenTabForActiveBook() {
        val playback =
            AudioPlaybackState(
                ebookId = "ebook-1",
                status = PlaybackStatus.PLAYING,
                segmentIndex = 2,
                totalSegments = 10,
            )

        assertFalse(
            shouldShowMiniPlayerBar(
                playbackState = playback,
                onListenTab = true,
                selectedEbookId = "ebook-1",
            ),
        )
    }

    @Test
    fun showsMiniPlayerOnListenTabWhenDifferentBookSelected() {
        val playback =
            AudioPlaybackState(
                ebookId = "ebook-1",
                status = PlaybackStatus.PAUSED,
                segmentIndex = 0,
                totalSegments = 5,
            )

        assertTrue(
            shouldShowMiniPlayerBar(
                playbackState = playback,
                onListenTab = true,
                selectedEbookId = "ebook-2",
            ),
        )
    }

    @Test
    fun hidesMiniPlayerWhenPlaybackIsIdle() {
        assertFalse(
            shouldShowMiniPlayerBar(
                playbackState = AudioPlaybackState(status = PlaybackStatus.IDLE),
                onListenTab = false,
                selectedEbookId = null,
            ),
        )
    }
}
