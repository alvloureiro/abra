package com.abra.data.repository

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.abra.domain.model.LanguageOption
import com.abra.domain.model.VoiceOption
import com.abra.domain.model.VoiceProfile
import com.abra.domain.model.VoiceSettings
import com.abra.domain.repository.VoiceSettingsRepository
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.suspendCancellableCoroutine

class DataStoreVoiceSettingsRepository(
    private val context: Context,
    private val dataStore: DataStore<Preferences>
) : VoiceSettingsRepository {
    private val initMutex = Mutex()
    private var textToSpeech: TextToSpeech? = null

    override fun observeSettings(): Flow<VoiceSettings> {
        return dataStore.data.map { preferences ->
            VoiceSettings(
                language = LanguageOption.fromTag(preferences[LANGUAGE_KEY]),
                voiceProfile = preferences[VOICE_PROFILE_KEY]
                    ?.let { runCatching { VoiceProfile.valueOf(it) }.getOrNull() }
                    ?: VoiceProfile.SYSTEM,
                voiceId = preferences[VOICE_ID_KEY]
            )
        }
    }

    override suspend fun updateLanguage(language: LanguageOption) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.tag
            preferences.remove(VOICE_ID_KEY)
        }
    }

    override suspend fun updateVoiceProfile(profile: VoiceProfile) {
        dataStore.edit { preferences ->
            preferences[VOICE_PROFILE_KEY] = profile.name
            preferences.remove(VOICE_ID_KEY)
        }
    }

    override suspend fun updateVoiceId(voiceId: String?) {
        dataStore.edit { preferences ->
            if (voiceId == null) {
                preferences.remove(VOICE_ID_KEY)
            } else {
                preferences[VOICE_ID_KEY] = voiceId
            }
        }
    }

    override suspend fun availableVoices(language: LanguageOption): List<VoiceOption> {
        val tts = ensureTextToSpeech()
        val locale = Locale.forLanguageTag(language.tag)
        val voices = tts.voices.orEmpty()
            .filter { voice ->
                voice.locale.language == locale.language &&
                    (locale.country.isBlank() || voice.locale.country == locale.country)
            }
            .map { voice ->
                VoiceOption(
                    id = voice.name,
                    name = voice.name,
                    language = language,
                    profile = voice.name.inferProfile(),
                    requiresNetwork = voice.isNetworkConnectionRequired
                )
            }
            .sortedWith(compareBy<VoiceOption> { it.requiresNetwork }.thenBy { it.name })

        return voices.ifEmpty {
            listOf(
                VoiceOption(
                    id = SYSTEM_VOICE_ID,
                    name = "System default",
                    language = language,
                    profile = VoiceProfile.SYSTEM,
                    requiresNetwork = false
                )
            )
        }
    }

    private suspend fun ensureTextToSpeech(): TextToSpeech {
        textToSpeech?.let { return it }
        return initMutex.withLock {
            textToSpeech ?: createTextToSpeech().also { textToSpeech = it }
        }
    }

    private suspend fun createTextToSpeech(): TextToSpeech {
        return suspendCancellableCoroutine { continuation ->
            var engine: TextToSpeech? = null
            engine = TextToSpeech(context.applicationContext) { status ->
                val initializedEngine = engine
                if (status == TextToSpeech.SUCCESS && initializedEngine != null) {
                    continuation.resume(initializedEngine)
                } else {
                    continuation.resumeWithException(
                        IllegalStateException("Android TextToSpeech is not available.")
                    )
                }
            }
            continuation.invokeOnCancellation { engine?.shutdown() }
        }
    }

    private fun String.inferProfile(): VoiceProfile {
        val normalized = lowercase(Locale.US)
        return when {
            "female" in normalized || "woman" in normalized -> VoiceProfile.FEMALE
            "male" in normalized || "man" in normalized -> VoiceProfile.MALE
            "neutral" in normalized -> VoiceProfile.NEUTRAL
            else -> VoiceProfile.SYSTEM
        }
    }

    private companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val VOICE_PROFILE_KEY = stringPreferencesKey("voice_profile")
        val VOICE_ID_KEY = stringPreferencesKey("voice_id")
        const val SYSTEM_VOICE_ID = "system"
    }
}
