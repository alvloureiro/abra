package com.abra.presentation.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abra.domain.model.PlaybackStatus
import com.abra.domain.usecase.toProgress
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReaderViewModel
    @Inject
    constructor(
        private val queryUseCases: ReaderQueryUseCases,
        private val playbackUseCases: ReaderPlaybackUseCases,
        private val progressUseCases: ReaderProgressUseCases,
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
                            queryUseCases.getEbook(ebookId),
                            queryUseCases.getEbookText(ebookId),
                            progressUseCases.observeProgress(ebookId),
                            queryUseCases.observeVoiceSettings(),
                            playbackUseCases.observePlaybackState(),
                        ) { ebook, segments, progress, settings, playback ->
                            ReaderUiState(
                                ebook = ebook,
                                segments = segments,
                                progress = progress,
                                voiceSettings = settings,
                                playbackState = playback,
                                isLoading = false,
                                errorMessage = null,
                            )
                        }
                    }.collect { state -> _uiState.value = state }
            }

            viewModelScope.launch {
                playbackUseCases.observePlaybackState().collect { playbackState ->
                    val progress =
                        playbackState.toProgress(System.currentTimeMillis())
                            ?: return@collect
                    if (playbackState.status.shouldPersistProgress()) {
                        progressUseCases.saveProgress(progress)
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
                playbackUseCases.startListening(
                    ebookId = ebook.id,
                    segments = state.segments,
                    startSegmentIndex = state.startSegmentIndex(),
                    settings = state.voiceSettings,
                )
            }
        }

        fun pause() {
            playbackUseCases.pauseListening()
        }

        fun resume() {
            playbackUseCases.resumeListening()
        }

        fun stop() {
            playbackUseCases.stopListening()
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
            val currentIndex =
                state.playbackState.segmentIndex
                    .takeIf { state.playbackState.ebookId == state.ebook?.id }
                    ?: state.progress?.segmentIndex
                    ?: 0
            val targetIndex =
                (currentIndex + delta).coerceIn(
                    minimumValue = 0,
                    maximumValue = state.segments.lastIndex,
                )
            playbackUseCases.skipListening(targetIndex)
        }

        private fun ReaderUiState.startSegmentIndex(): Int {
            val activePlaybackIndex =
                playbackState.segmentIndex.takeIf {
                    playbackState.ebookId == ebook?.id &&
                        playbackState.status != PlaybackStatus.COMPLETED
                }
            val savedProgressIndex = progress?.takeUnless { it.completed }?.segmentIndex
            return (activePlaybackIndex ?: savedProgressIndex ?: 0).coerceIn(0, segments.lastIndex)
        }

        private fun PlaybackStatus.shouldPersistProgress(): Boolean =
            this == PlaybackStatus.PLAYING ||
                this == PlaybackStatus.PAUSED ||
                this == PlaybackStatus.STOPPED ||
                this == PlaybackStatus.COMPLETED
    }
