package com.abra.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.abra.domain.model.LanguageOption
import com.abra.domain.model.VoiceProfile
import com.abra.domain.model.VoiceSettings
import com.abra.domain.repository.VoiceSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreVoiceSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : VoiceSettingsRepository {
    override fun observeSettings(): Flow<VoiceSettings> =
        dataStore.data.map { preferences ->
            VoiceSettings(
                language = LanguageOption.fromTag(preferences[LANGUAGE_KEY]),
                voiceProfile =
                    preferences[VOICE_PROFILE_KEY]
                        ?.let { runCatching { VoiceProfile.valueOf(it) }.getOrNull() }
                        ?: VoiceProfile.SYSTEM,
                voiceId = preferences[VOICE_ID_KEY],
            )
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

    private companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val VOICE_PROFILE_KEY = stringPreferencesKey("voice_profile")
        val VOICE_ID_KEY = stringPreferencesKey("voice_id")
    }
}
