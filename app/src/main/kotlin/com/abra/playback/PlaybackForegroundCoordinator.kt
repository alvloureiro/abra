package com.abra.playback

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.PlaybackStatus
import com.abra.domain.repository.AudioPlaybackEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Singleton
class PlaybackForegroundCoordinator
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val engine: AudioPlaybackEngine,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        fun start() {
            scope.launch {
                engine.playbackState
                    .map { it.status }
                    .distinctUntilChanged()
                    .collect { status ->
                        if (status.requiresForegroundService()) {
                            startPlaybackService()
                        }
                    }
            }
        }

        private fun startPlaybackService() {
            val intent = Intent(context, ListeningPlaybackService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

private fun PlaybackStatus.requiresForegroundService(): Boolean =
    this == PlaybackStatus.LOADING ||
        this == PlaybackStatus.PLAYING ||
        this == PlaybackStatus.PAUSED

internal fun AudioPlaybackState.shouldStopForegroundService(): Boolean =
    status == PlaybackStatus.STOPPED ||
        status == PlaybackStatus.IDLE ||
        status == PlaybackStatus.COMPLETED ||
        status == PlaybackStatus.ERROR

internal fun AudioPlaybackState.shouldRunForegroundService(): Boolean =
    status == PlaybackStatus.LOADING ||
        status == PlaybackStatus.PLAYING ||
        status == PlaybackStatus.PAUSED
