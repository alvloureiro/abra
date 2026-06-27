package com.abra.playback

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.PlaybackStatus
import com.abra.domain.repository.AudioPlaybackEngine

internal class PlaybackMediaSession(
    private val engine: AudioPlaybackEngine,
    val session: MediaSessionCompat,
) {
    fun attach() {
        session.setCallback(
            object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    engine.resume()
                }

                override fun onPause() {
                    engine.pause()
                }

                override fun onStop() {
                    engine.stop()
                }
            },
        )
        session.isActive = true
    }

    fun update(state: AudioPlaybackState) {
        val playbackState =
            when (state.status) {
                PlaybackStatus.PLAYING ->
                    PlaybackStateCompat.STATE_PLAYING
                PlaybackStatus.PAUSED ->
                    PlaybackStateCompat.STATE_PAUSED
                PlaybackStatus.LOADING ->
                    PlaybackStateCompat.STATE_BUFFERING
                else ->
                    PlaybackStateCompat.STATE_STOPPED
            }

        session.setPlaybackState(
            PlaybackStateCompat
                .Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_STOP,
                ).setState(playbackState, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1f)
                .build(),
        )
    }

    fun release() {
        session.isActive = false
        session.release()
    }
}
