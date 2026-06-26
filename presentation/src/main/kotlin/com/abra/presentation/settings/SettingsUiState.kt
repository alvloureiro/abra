package com.abra.presentation.settings

import com.abra.domain.model.VoiceOption
import com.abra.domain.model.VoiceSettings

data class SettingsUiState(
    val settings: VoiceSettings = VoiceSettings(),
    val availableVoices: List<VoiceOption> = emptyList(),
    val isLoadingVoices: Boolean = true,
    val errorMessage: String? = null,
)
