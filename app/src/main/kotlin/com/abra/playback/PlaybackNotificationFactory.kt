package com.abra.playback

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.abra.MainActivity
import com.abra.R
import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.PlaybackStatus

internal object PlaybackNotificationFactory {
    const val CHANNEL_ID = "abra_playback"
    const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE)
                as android.app.NotificationManager
        val channel =
            android.app.NotificationChannel(
                CHANNEL_ID,
                "Listening playback",
                android.app.NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Shows playback controls while Abra reads aloud."
            }
        manager.createNotificationChannel(channel)
    }

    fun build(
        context: Context,
        mediaSession: MediaSessionCompat,
        state: AudioPlaybackState,
        title: String,
    ): Notification {
        val contentIntent =
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        return NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_playback)
            .setContentTitle(title)
            .setContentText(segmentLabel(state))
            .setContentIntent(contentIntent)
            .setOngoing(
                state.status == PlaybackStatus.PLAYING ||
                    state.status == PlaybackStatus.LOADING,
            )
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0, 1),
            ).build()
    }

    private fun segmentLabel(state: AudioPlaybackState): String {
        if (state.totalSegments <= 0) return "Preparing playback"
        return "Segment ${state.segmentIndex + 1} of ${state.totalSegments}"
    }
}
