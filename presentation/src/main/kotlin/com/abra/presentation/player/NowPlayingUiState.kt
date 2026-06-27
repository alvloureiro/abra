package com.abra.presentation.player

import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.PlaybackStatus

data class NowPlayingUiState(
    val playbackState: AudioPlaybackState = AudioPlaybackState(),
    val ebookTitle: String? = null,
) {
    val segmentLabel: String
        get() {
            val total = playbackState.totalSegments
            if (total <= 0) return "Listening"
            val segment = playbackState.segmentIndex.coerceIn(0, total - 1)
            return "Segment ${segment + 1} of $total"
        }

    val progress: Float
        get() {
            val total = playbackState.totalSegments
            if (total <= 0) return 0f
            if (playbackState.status == PlaybackStatus.COMPLETED) return 1f
            val index = playbackState.segmentIndex.coerceIn(0, total - 1)
            return ((index + 1).toFloat() / total).coerceIn(0f, 1f)
        }
}

fun AudioPlaybackState.shouldShowMiniPlayer(): Boolean =
    status == PlaybackStatus.PLAYING ||
        status == PlaybackStatus.PAUSED ||
        status == PlaybackStatus.LOADING

fun shouldShowMiniPlayerBar(
    playbackState: AudioPlaybackState,
    onListenTab: Boolean,
    selectedEbookId: String?,
): Boolean {
    if (!playbackState.shouldShowMiniPlayer()) return false
    val onFullPlayerForActiveBook =
        onListenTab && selectedEbookId == playbackState.ebookId
    return !onFullPlayerForActiveBook
}
