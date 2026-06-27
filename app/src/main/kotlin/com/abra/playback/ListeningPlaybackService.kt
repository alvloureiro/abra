package com.abra.playback

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import com.abra.domain.repository.AudioPlaybackEngine
import com.abra.domain.repository.EbookRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ListeningPlaybackService : Service() {
    @Inject lateinit var engine: AudioPlaybackEngine

    @Inject lateinit var ebookRepository: EbookRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var playbackMediaSession: PlaybackMediaSession
    private var observeJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        PlaybackNotificationFactory.createChannel(this)
        playbackMediaSession =
            PlaybackMediaSession(
                engine = engine,
                session = MediaSessionCompat(this, "AbraPlayback"),
            )
        playbackMediaSession.attach()
        observeJob = serviceScope.launch { observePlayback() }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val state = engine.playbackState.value
        startForeground(
            PlaybackNotificationFactory.NOTIFICATION_ID,
            PlaybackNotificationFactory.build(
                context = this,
                mediaSession = playbackMediaSession.session,
                state = state,
                title = "Listening",
            ),
        )
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        observeJob?.cancel()
        serviceScope.cancel()
        playbackMediaSession.release()
        super.onDestroy()
    }

    private suspend fun observePlayback() {
        engine.playbackState
            .flatMapLatest { state ->
                val ebookId = state.ebookId
                if (ebookId == null) {
                    flowOf(state to "Listening")
                } else {
                    ebookRepository.observeEbook(ebookId).map { ebook ->
                        state to (ebook?.metadata?.title ?: "Listening")
                    }
                }
            }.collect { (state, title) ->
                if (state.shouldStopForegroundService()) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    return@collect
                }
                if (!state.shouldRunForegroundService()) return@collect

                playbackMediaSession.update(state)
                val notification =
                    PlaybackNotificationFactory.build(
                        context = this@ListeningPlaybackService,
                        mediaSession = playbackMediaSession.session,
                        state = state,
                        title = title,
                    )
                startForeground(PlaybackNotificationFactory.NOTIFICATION_ID, notification)
            }
    }
}
