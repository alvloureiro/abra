package com.abra

import android.app.Application
import com.abra.playback.PlaybackForegroundCoordinator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AbraApplication : Application() {
    @Inject lateinit var playbackForegroundCoordinator: PlaybackForegroundCoordinator

    override fun onCreate() {
        super.onCreate()
        playbackForegroundCoordinator.start()
    }
}
