package com.abra.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class AndroidTtsEngineProvider
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
    private val initMutex = Mutex()
    private var textToSpeech: TextToSpeech? = null

    suspend fun getEngine(): TextToSpeech {
        textToSpeech?.let { return it }
        return initMutex.withLock {
            textToSpeech ?: createTextToSpeech().also { textToSpeech = it }
        }
    }

    private suspend fun createTextToSpeech(): TextToSpeech =
        suspendCancellableCoroutine { continuation ->
            var engine: TextToSpeech? = null
            engine =
                TextToSpeech(context.applicationContext) { status ->
                    val initializedEngine = engine
                    if (status == TextToSpeech.SUCCESS && initializedEngine != null) {
                        continuation.resume(initializedEngine)
                    } else {
                        continuation.resumeWithException(
                            IllegalStateException("Android TextToSpeech is not available."),
                        )
                    }
                }
            continuation.invokeOnCancellation {
                engine?.shutdown()
            }
        }
}
