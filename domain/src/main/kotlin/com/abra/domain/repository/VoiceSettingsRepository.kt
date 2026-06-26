package com.abra.domain.repository

import com.abra.domain.model.LanguageOption
import com.abra.domain.model.VoiceOption
import com.abra.domain.model.VoiceProfile
import com.abra.domain.model.VoiceSettings
import kotlinx.coroutines.flow.Flow

interface VoiceSettingsRepository {
    fun observeSettings(): Flow<VoiceSettings>

    suspend fun updateLanguage(language: LanguageOption)

    suspend fun updateVoiceProfile(profile: VoiceProfile)

    suspend fun updateVoiceId(voiceId: String?)

    suspend fun availableVoices(language: LanguageOption): List<VoiceOption>
}
