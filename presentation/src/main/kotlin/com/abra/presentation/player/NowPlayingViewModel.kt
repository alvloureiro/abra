package com.abra.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abra.domain.usecase.NowPlayingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NowPlayingViewModel
    @Inject
    constructor(
        private val useCases: NowPlayingUseCases,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(NowPlayingUiState())
        val uiState: StateFlow<NowPlayingUiState> = _uiState.asStateFlow()

        init {
            viewModelScope.launch {
                val playbackState = useCases.observePlaybackState()
                combine(
                    playbackState,
                    playbackState
                        .map { it.ebookId }
                        .distinctUntilChanged()
                        .flatMapLatest { ebookId ->
                            if (ebookId == null) {
                                flowOf(null)
                            } else {
                                useCases.getEbook(ebookId)
                            }
                        },
                ) { playback, ebook ->
                    NowPlayingUiState(
                        playbackState = playback,
                        ebookTitle = ebook?.metadata?.title,
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            }
        }

        fun pause() {
            useCases.pauseListening()
        }

        fun resume() {
            useCases.resumeListening()
        }
    }
