package com.abra.domain.usecase

import com.abra.domain.model.LanguageOption
import com.abra.domain.model.VoiceOption
import com.abra.domain.model.VoiceProfile
import com.abra.domain.model.VoiceSettings
import com.abra.domain.repository.VoiceCatalog
import com.abra.domain.repository.VoiceSettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveVoiceSettingsUseCase(
    private val voiceSettingsRepository: VoiceSettingsRepository,
) {
    operator fun invoke(): Flow<VoiceSettings> = voiceSettingsRepository.observeSettings()
}

class UpdateVoiceLanguageUseCase(
    private val voiceSettingsRepository: VoiceSettingsRepository,
) {
    suspend operator fun invoke(language: LanguageOption) {
        voiceSettingsRepository.updateLanguage(language)
    }
}

class UpdateVoiceProfileUseCase(
    private val voiceSettingsRepository: VoiceSettingsRepository,
) {
    suspend operator fun invoke(profile: VoiceProfile) {
        voiceSettingsRepository.updateVoiceProfile(profile)
    }
}

class UpdateVoiceIdUseCase(
    private val voiceSettingsRepository: VoiceSettingsRepository,
) {
    suspend operator fun invoke(voiceId: String?) {
        voiceSettingsRepository.updateVoiceId(voiceId)
    }
}

class GetAvailableVoicesUseCase(
    private val voiceCatalog: VoiceCatalog,
) {
    suspend operator fun invoke(language: LanguageOption): List<VoiceOption> =
        voiceCatalog.availableVoices(language)
}
