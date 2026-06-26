package com.abra.data.repository

import com.abra.data.tts.AndroidTtsEngineProvider
import com.abra.domain.model.LanguageOption
import com.abra.domain.model.VoiceOption
import com.abra.domain.model.VoiceProfile
import com.abra.domain.repository.VoiceCatalog
import java.util.Locale

class AndroidTtsVoiceCatalog(
    private val ttsEngineProvider: AndroidTtsEngineProvider,
) : VoiceCatalog {
    override suspend fun availableVoices(language: LanguageOption): List<VoiceOption> {
        val tts = ttsEngineProvider.getEngine()
        val locale = Locale.forLanguageTag(language.tag)
        val voices =
            tts.voices
                .orEmpty()
                .filter { voice ->
                    voice.locale.language == locale.language &&
                        (locale.country.isBlank() || voice.locale.country == locale.country)
                }.map { voice ->
                    VoiceOption(
                        id = voice.name,
                        name = voice.name,
                        language = language,
                        profile = voice.name.inferProfile(),
                        requiresNetwork = voice.isNetworkConnectionRequired,
                    )
                }.sortedWith(compareBy<VoiceOption> { it.requiresNetwork }.thenBy { it.name })

        return voices.ifEmpty {
            listOf(
                VoiceOption(
                    id = SYSTEM_VOICE_ID,
                    name = "System default",
                    language = language,
                    profile = VoiceProfile.SYSTEM,
                    requiresNetwork = false,
                ),
            )
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
        const val SYSTEM_VOICE_ID = "system"
    }
}
