package com.abra.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abra.domain.model.LanguageOption
import com.abra.domain.model.VoiceProfile
import com.abra.domain.usecase.GetAvailableVoicesUseCase
import com.abra.domain.usecase.ObserveVoiceSettingsUseCase
import com.abra.domain.usecase.UpdateVoiceIdUseCase
import com.abra.domain.usecase.UpdateVoiceLanguageUseCase
import com.abra.domain.usecase.UpdateVoiceProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeVoiceSettings: ObserveVoiceSettingsUseCase,
    private val updateVoiceLanguage: UpdateVoiceLanguageUseCase,
    private val updateVoiceProfile: UpdateVoiceProfileUseCase,
    private val updateVoiceId: UpdateVoiceIdUseCase,
    private val getAvailableVoices: GetAvailableVoicesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeVoiceSettings().collectLatest { settings ->
                _uiState.update {
                    it.copy(settings = settings, isLoadingVoices = true, errorMessage = null)
                }
                runCatching { getAvailableVoices(settings.language) }
                    .onSuccess { voices ->
                        _uiState.update {
                            it.copy(
                                availableVoices = voices,
                                isLoadingVoices = false,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                availableVoices = emptyList(),
                                isLoadingVoices = false,
                                errorMessage = error.message ?: "Unable to load voices."
                            )
                        }
                    }
            }
        }
    }

    fun selectLanguage(language: LanguageOption) {
        viewModelScope.launch {
            updateVoiceLanguage(language)
        }
    }

    fun selectVoiceProfile(profile: VoiceProfile) {
        viewModelScope.launch {
            updateVoiceProfile(profile)
        }
    }

    fun selectVoice(voiceId: String?) {
        viewModelScope.launch {
            updateVoiceId(voiceId)
        }
    }
}
