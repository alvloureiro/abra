package com.abra.presentation.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abra.domain.model.LanguageOption
import com.abra.domain.model.VoiceOption
import com.abra.domain.model.VoiceProfile

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onLanguageSelected = viewModel::selectLanguage,
        onVoiceProfileSelected = viewModel::selectVoiceProfile,
        onVoiceSelected = viewModel::selectVoice,
        modifier = modifier
    )
}

@Composable
private fun SettingsScreen(
    state: SettingsUiState,
    onLanguageSelected: (LanguageOption) -> Unit,
    onVoiceProfileSelected: (VoiceProfile) -> Unit,
    onVoiceSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Voice Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        SettingsSection(title = "Language") {
            HorizontalOptions {
                LanguageOption.entries.forEach { language ->
                    FilterChip(
                        selected = state.settings.language == language,
                        onClick = { onLanguageSelected(language) },
                        label = { Text(language.displayName) }
                    )
                }
            }
        }

        SettingsSection(title = "Voice profile") {
            HorizontalOptions {
                VoiceProfile.entries.forEach { profile ->
                    FilterChip(
                        selected = state.settings.voiceProfile == profile,
                        onClick = { onVoiceProfileSelected(profile) },
                        label = { Text(profile.displayName) }
                    )
                }
            }
        }

        SettingsSection(title = "Available voices") {
            when {
                state.isLoadingVoices -> CircularProgressIndicator()
                state.errorMessage != null -> Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
                else -> VoiceList(
                    voices = state.availableVoices.filterForProfile(state.settings.voiceProfile),
                    selectedVoiceId = state.settings.voiceId,
                    onVoiceSelected = onVoiceSelected
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@Composable
private fun HorizontalOptions(
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
private fun VoiceList(
    voices: List<VoiceOption>,
    selectedVoiceId: String?,
    onVoiceSelected: (String?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(
            items = voices,
            key = { it.id }
        ) { voice ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RadioButton(
                    selected = selectedVoiceId == voice.id || (selectedVoiceId == null && voice.id == "system"),
                    onClick = {
                        onVoiceSelected(if (voice.id == "system") null else voice.id)
                    }
                )
                Column {
                    Text(voice.name)
                    Text(
                        text = "${voice.profile.displayName} - ${if (voice.requiresNetwork) "Network" else "Local/system"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun List<VoiceOption>.filterForProfile(profile: VoiceProfile): List<VoiceOption> {
    if (profile == VoiceProfile.SYSTEM) return this
    return filter { it.profile == profile }.ifEmpty { this }
}
