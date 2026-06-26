package com.abra.domain.repository

import com.abra.domain.model.LanguageOption
import com.abra.domain.model.VoiceOption

interface VoiceCatalog {
    suspend fun availableVoices(language: LanguageOption): List<VoiceOption>
}
