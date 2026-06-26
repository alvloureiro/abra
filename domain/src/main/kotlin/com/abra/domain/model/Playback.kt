package com.abra.domain.model

data class PlaybackRequest(
    val ebookId: String,
    val segments: List<ListeningSegment>,
    val startSegmentIndex: Int,
    val settings: VoiceSettings,
)

data class AudioPlaybackState(
    val ebookId: String? = null,
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val segmentIndex: Int = 0,
    val totalSegments: Int = 0,
    val message: String? = null,
) {
    val isActive: Boolean
        get() = status == PlaybackStatus.PLAYING || status == PlaybackStatus.PAUSED
}

enum class PlaybackStatus {
    IDLE,
    LOADING,
    PLAYING,
    PAUSED,
    STOPPED,
    COMPLETED,
    ERROR,
}
