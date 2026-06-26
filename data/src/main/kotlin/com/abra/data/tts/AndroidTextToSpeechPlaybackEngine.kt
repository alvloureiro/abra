package com.abra.data.tts

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.abra.domain.model.AudioPlaybackState
import com.abra.domain.model.ListeningSegment
import com.abra.domain.model.PlaybackRequest
import com.abra.domain.model.PlaybackStatus
import com.abra.domain.model.VoiceSettings
import com.abra.domain.repository.AudioPlaybackEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale

class AndroidTextToSpeechPlaybackEngine(
    private val ttsEngineProvider: AndroidTtsEngineProvider,
) : AudioPlaybackEngine {
    private val listenerMutex = Mutex()
    private var textToSpeech: TextToSpeech? = null
    private var currentSegments: List<ListeningSegment> = emptyList()
    private var currentSettings: VoiceSettings = VoiceSettings()
    private var currentEbookId: String? = null
    private var currentSegmentIndex: Int = 0
    private var currentUtteranceId: String? = null

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    override val playbackState: StateFlow<AudioPlaybackState> = _playbackState

    override suspend fun play(request: PlaybackRequest) {
        val tts = ensureTextToSpeech()
        currentEbookId = request.ebookId
        currentSegments = request.segments
        currentSettings = request.settings
        currentSegmentIndex =
            request.startSegmentIndex.coerceIn(
                0,
                (request.segments.lastIndex).coerceAtLeast(0),
            )

        if (request.segments.isEmpty()) {
            _playbackState.value =
                AudioPlaybackState(
                    ebookId = request.ebookId,
                    status = PlaybackStatus.ERROR,
                    message = "This ebook has no extracted text to play.",
                )
            return
        }

        applySettings(tts, request.settings)
        _playbackState.value =
            AudioPlaybackState(
                ebookId = request.ebookId,
                status = PlaybackStatus.LOADING,
                segmentIndex = currentSegmentIndex,
                totalSegments = currentSegments.size,
            )
        speakCurrent(tts)
    }

    override fun pause() {
        if (_playbackState.value.status != PlaybackStatus.PLAYING) return
        currentUtteranceId = null
        textToSpeech?.stop()
        _playbackState.value = _playbackState.value.copy(status = PlaybackStatus.PAUSED)
    }

    override fun resume() {
        if (_playbackState.value.status != PlaybackStatus.PAUSED) return
        val tts = textToSpeech ?: return
        applySettings(tts, currentSettings)
        speakCurrent(tts)
    }

    override fun stop() {
        currentUtteranceId = null
        textToSpeech?.stop()
        _playbackState.value = _playbackState.value.copy(status = PlaybackStatus.STOPPED)
    }

    override fun skipTo(segmentIndex: Int) {
        if (currentSegments.isEmpty()) return
        currentSegmentIndex = segmentIndex.coerceIn(0, currentSegments.lastIndex)
        val currentState = _playbackState.value
        if (currentState.status == PlaybackStatus.PLAYING) {
            currentUtteranceId = null
            textToSpeech?.stop()
            textToSpeech?.let { speakCurrent(it) }
        } else {
            _playbackState.value =
                currentState.copy(
                    segmentIndex = currentSegmentIndex,
                    totalSegments = currentSegments.size,
                )
        }
    }

    private suspend fun ensureTextToSpeech(): TextToSpeech {
        textToSpeech?.let { return it }
        return listenerMutex.withLock {
            textToSpeech ?: ttsEngineProvider.getEngine().also { engine ->
                textToSpeech = engine
                engine.setOnUtteranceProgressListener(playbackListener)
            }
        }
    }

    private fun applySettings(
        tts: TextToSpeech,
        settings: VoiceSettings,
    ) {
        val locale = Locale.forLanguageTag(settings.language.tag)
        if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
            tts.language = locale
        }

        val voiceId = settings.voiceId
        if (!voiceId.isNullOrBlank() && voiceId != SYSTEM_VOICE_ID) {
            tts.voices.orEmpty().firstOrNull { it.name == voiceId }?.let { voice ->
                tts.voice = voice
            }
        }
    }

    private fun speakCurrent(tts: TextToSpeech) {
        val ebookId = currentEbookId ?: return
        val segment = currentSegments.getOrNull(currentSegmentIndex)
        if (segment == null) {
            _playbackState.value =
                AudioPlaybackState(
                    ebookId = ebookId,
                    status = PlaybackStatus.COMPLETED,
                    segmentIndex = currentSegments.lastIndex.coerceAtLeast(0),
                    totalSegments = currentSegments.size,
                )
            return
        }

        val utteranceId = "$ebookId-$currentSegmentIndex-${System.nanoTime()}"
        currentUtteranceId = utteranceId
        _playbackState.value =
            AudioPlaybackState(
                ebookId = ebookId,
                status = PlaybackStatus.PLAYING,
                segmentIndex = currentSegmentIndex,
                totalSegments = currentSegments.size,
            )
        tts.speak(
            segment.text,
            TextToSpeech.QUEUE_FLUSH,
            Bundle.EMPTY,
            utteranceId,
        )
    }

    private val playbackListener =
        object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit

            override fun onDone(utteranceId: String?) {
                if (utteranceId == null || utteranceId != currentUtteranceId) return
                if (_playbackState.value.status != PlaybackStatus.PLAYING) return

                currentSegmentIndex += 1
                val tts = textToSpeech ?: return
                speakCurrent(tts)
            }

            @Deprecated("Deprecated in Android framework")
            override fun onError(utteranceId: String?) {
                _playbackState.value =
                    _playbackState.value.copy(
                        status = PlaybackStatus.ERROR,
                        message = "TextToSpeech playback failed.",
                    )
            }

            override fun onError(
                utteranceId: String?,
                errorCode: Int,
            ) {
                _playbackState.value =
                    _playbackState.value.copy(
                        status = PlaybackStatus.ERROR,
                        message = "TextToSpeech playback failed with code $errorCode.",
                    )
            }
        }

    private companion object {
        const val SYSTEM_VOICE_ID = "system"
    }
}
