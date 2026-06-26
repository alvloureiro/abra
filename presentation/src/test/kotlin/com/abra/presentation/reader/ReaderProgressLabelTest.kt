package com.abra.presentation.reader

import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.PlaybackStatus
import org.junit.Assert.assertTrue
import org.junit.Test

class ReaderProgressLabelTest {
    @Test
    fun playbackStateExposesActiveStatusForPausedAndPlaying() {
        assertTrue(AudioPlaybackState(status = PlaybackStatus.PLAYING).isActive)
        assertTrue(AudioPlaybackState(status = PlaybackStatus.PAUSED).isActive)
    }
}
