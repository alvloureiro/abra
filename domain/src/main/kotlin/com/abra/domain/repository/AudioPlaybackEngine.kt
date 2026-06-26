package com.abra.domain.repository

import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.PlaybackRequest
import kotlinx.coroutines.flow.StateFlow

interface AudioPlaybackEngine {
    val playbackState: StateFlow<AudioPlaybackState>

    suspend fun play(request: PlaybackRequest)
    fun pause()
    fun resume()
    fun stop()
    fun skipTo(segmentIndex: Int)
}
