package com.abra.presentation.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abra.domain.model.ListeningProgress
import com.abra.domain.model.PlaybackStatus
import com.abra.domain.usecase.GetEbookTextUseCase
import com.abra.domain.usecase.GetEbookUseCase
import com.abra.domain.usecase.ObserveListeningProgressUseCase
import com.abra.domain.usecase.ObservePlaybackStateUseCase
import com.abra.domain.usecase.ObserveVoiceSettingsUseCase
import com.abra.domain.usecase.PauseListeningUseCase
import com.abra.domain.usecase.ResumeListeningUseCase
import com.abra.domain.usecase.SaveListeningProgressUseCase
import com.abra.domain.usecase.SkipListeningUseCase
import com.abra.domain.usecase.StartListeningUseCase
import com.abra.domain.usecase.StopListeningUseCase
import com.abra.domain.usecase.toProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val getEbook: GetEbookUseCase,
    private val getEbookText: GetEbookTextUseCase,
    private val observeProgress: ObserveListeningProgressUseCase,
    private val observePlaybackState: ObservePlaybackStateUseCase,
    private val observeVoiceSettings: ObserveVoiceSettingsUseCase,
    private val startListening: StartListeningUseCase,
    private val pauseListening: PauseListeningUseCase,
    private val resumeListening: ResumeListeningUseCase,
    private val stopListening: StopListeningUseCase,
    private val skipListening: SkipListeningUseCase,
    private val saveProgress: SaveListeningProgressUseCase
) : ViewModel() {
    private val selectedEbookId = MutableStateFlow<String?>(null)
    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            selectedEbookId
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { ebookId ->
                    combine(
                        getEbook(ebookId),
                        getEbookText(ebookId),
                        observeProgress(ebookId),
                        observeVoiceSettings(),
                        observePlaybackState()
                    ) { ebook, segments, progress, settings, playback ->
                        ReaderUiState(
                            ebook = ebook,
                            segments = segments,
                            progress = progress,
                            voiceSettings = settings,
                            playbackState = playback,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .collect { state -> _uiState.value = state }
        }

        viewModelScope.launch {
            observePlaybackState().collect { playbackState ->
                val progress = playbackState.toProgress(System.currentTimeMillis())
                    ?: return@collect
                if (playbackState.status.shouldPersistProgress()) {
                    saveProgress(progress)
                }
            }
        }
    }

    fun openEbook(ebookId: String) {
        selectedEbookId.value = ebookId
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
    }

    fun play() {
        val state = uiState.value
        val ebook = state.ebook ?: return
        if (state.segments.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "This ebook has no extracted text to play.")
            }
            return
        }

        viewModelScope.launch {
            startListening(
                ebookId = ebook.id,
                segments = state.segments,
                startSegmentIndex = state.startSegmentIndex(),
                settings = state.voiceSettings
            )
        }
    }

    fun pause() {
        pauseListening()
    }

    fun resume() {
        resumeListening()
    }

    fun stop() {
        stopListening()
    }

    fun skipBackward() {
        skipBy(delta = -1)
    }

    fun skipForward() {
        skipBy(delta = 1)
    }

    private fun skipBy(delta: Int) {
        val state = uiState.value
        if (state.segments.isEmpty()) return
        val currentIndex = state.playbackState.segmentIndex
            .takeIf { state.playbackState.ebookId == state.ebook?.id }
            ?: state.progress?.segmentIndex
            ?: 0
        skipListening((currentIndex + delta).coerceIn(0, state.segments.lastIndex))
    }

    private fun ReaderUiState.startSegmentIndex(): Int {
        if (playbackState.ebookId == ebook?.id && playbackState.status != PlaybackStatus.COMPLETED) {
            return playbackState.segmentIndex.coerceIn(0, segments.lastIndex)
        }
        val savedProgress = progress ?: return 0
        if (savedProgress.completed) return 0
        return savedProgress.segmentIndex.coerceIn(0, segments.lastIndex)
    }

    private fun PlaybackStatus.shouldPersistProgress(): Boolean {
        return this == PlaybackStatus.PLAYING ||
            this == PlaybackStatus.PAUSED ||
            this == PlaybackStatus.STOPPED ||
            this == PlaybackStatus.COMPLETED
    }
}
