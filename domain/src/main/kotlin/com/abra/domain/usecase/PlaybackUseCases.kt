package com.abra.domain.usecase

import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.ListeningProgress
import com.abra.domain.model.ListeningSegment
import com.abra.domain.model.PlaybackRequest
import com.abra.domain.model.PlaybackStatus
import com.abra.domain.model.VoiceSettings
import com.abra.domain.repository.AudioPlaybackEngine
import com.abra.domain.repository.ListeningProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class ObservePlaybackStateUseCase(
    private val audioPlaybackEngine: AudioPlaybackEngine,
) {
    operator fun invoke(): StateFlow<AudioPlaybackState> = audioPlaybackEngine.playbackState
}

class StartListeningUseCase(
    private val audioPlaybackEngine: AudioPlaybackEngine,
) {
    suspend operator fun invoke(
        ebookId: String,
        segments: List<ListeningSegment>,
        startSegmentIndex: Int,
        settings: VoiceSettings,
    ) {
        audioPlaybackEngine.play(
            PlaybackRequest(
                ebookId = ebookId,
                segments = segments,
                startSegmentIndex = startSegmentIndex,
                settings = settings,
            ),
        )
    }
}

class PauseListeningUseCase(
    private val audioPlaybackEngine: AudioPlaybackEngine,
) {
    operator fun invoke() {
        audioPlaybackEngine.pause()
    }
}

class ResumeListeningUseCase(
    private val audioPlaybackEngine: AudioPlaybackEngine,
) {
    operator fun invoke() {
        audioPlaybackEngine.resume()
    }
}

class StopListeningUseCase(
    private val audioPlaybackEngine: AudioPlaybackEngine,
) {
    operator fun invoke() {
        audioPlaybackEngine.stop()
    }
}

class SkipListeningUseCase(
    private val audioPlaybackEngine: AudioPlaybackEngine,
) {
    operator fun invoke(segmentIndex: Int) {
        audioPlaybackEngine.skipTo(segmentIndex)
    }
}

class ObserveListeningProgressUseCase(
    private val listeningProgressRepository: ListeningProgressRepository,
) {
    operator fun invoke(ebookId: String): Flow<ListeningProgress?> =
        listeningProgressRepository.observeProgress(ebookId)
}

class SaveListeningProgressUseCase(
    private val listeningProgressRepository: ListeningProgressRepository,
) {
    suspend operator fun invoke(progress: ListeningProgress) {
        listeningProgressRepository.saveProgress(progress)
    }
}

fun AudioPlaybackState.toProgress(updatedAtEpochMillis: Long): ListeningProgress? {
    val activeEbookId = ebookId ?: return null
    if (status == PlaybackStatus.IDLE || status == PlaybackStatus.ERROR) return null
    return ListeningProgress(
        ebookId = activeEbookId,
        segmentIndex = segmentIndex.coerceAtLeast(0),
        characterOffset = 0,
        completed = status == PlaybackStatus.COMPLETED,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )
}
