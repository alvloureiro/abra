package com.abra.presentation.reader

import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.Ebook
import com.abra.domain.model.ListeningProgress
import com.abra.domain.model.ListeningSegment
import com.abra.domain.model.VoiceSettings

data class ReaderUiState(
    val ebook: Ebook? = null,
    val segments: List<ListeningSegment> = emptyList(),
    val progress: ListeningProgress? = null,
    val voiceSettings: VoiceSettings = VoiceSettings(),
    val playbackState: AudioPlaybackState = AudioPlaybackState(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val selectedEbookId: String?
        get() = ebook?.id
}
