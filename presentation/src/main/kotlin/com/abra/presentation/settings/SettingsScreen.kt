package com.abra.presentation.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abra.domain.model.LanguageOption
import com.abra.domain.model.VoiceOption
import com.abra.domain.model.VoiceProfile

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showLicenses by remember { mutableStateOf(false) }

    if (showLicenses) {
        OpenSourceLicensesScreen(
            onBack = { showLicenses = false },
            modifier = modifier,
        )
    } else {
        SettingsScreen(
            state = state,
            onLanguageSelected = viewModel::selectLanguage,
            onVoiceProfileSelected = viewModel::selectVoiceProfile,
            onVoiceSelected = viewModel::selectVoice,
            onOpenLicenses = { showLicenses = true },
            modifier = modifier,
        )
    }
}

@Composable
private fun SettingsScreen(
    state: SettingsUiState,
    onLanguageSelected: (LanguageOption) -> Unit,
    onVoiceProfileSelected: (VoiceProfile) -> Unit,
    onVoiceSelected: (String?) -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val versionName =
        remember {
            runCatching {
                val packageManager = context.packageManager
                val packageName = context.packageName
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(
                        packageName,
                        PackageManager.PackageInfoFlags.of(0),
                    ).versionName
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(packageName, 0).versionName
                }
            }.getOrNull().orEmpty()
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        VoiceSettingsContent(
            state = state,
            onLanguageSelected = onLanguageSelected,
            onVoiceProfileSelected = onVoiceProfileSelected,
            onVoiceSelected = onVoiceSelected,
        )

        AboutSection(
            versionName = versionName,
            onPrivacyPolicyClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL)),
                )
            },
            onSourceRepositoryClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_REPOSITORY_URL)),
                )
            },
            onOpenLicensesClick = onOpenLicenses,
        )
    }
}

@Composable
private fun VoiceSettingsContent(
    state: SettingsUiState,
    onLanguageSelected: (LanguageOption) -> Unit,
    onVoiceProfileSelected: (VoiceProfile) -> Unit,
    onVoiceSelected: (String?) -> Unit,
) {
    Text(
        text = "Voice Settings",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
    )

    SettingsSection(title = "Language") {
        HorizontalOptions {
            LanguageOption.entries.forEach { language ->
                FilterChip(
                    selected = state.settings.language == language,
                    onClick = { onLanguageSelected(language) },
                    label = { Text(language.displayName) },
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
                    label = { Text(profile.displayName) },
                )
            }
        }
    }

    SettingsSection(title = "Available voices") {
        when {
            state.isLoadingVoices -> CircularProgressIndicator()
            state.errorMessage != null ->
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                )
            else ->
                VoiceList(
                    voices =
                        state.availableVoices.filterForProfile(
                            state.settings.voiceProfile,
                        ),
                    selectedVoiceId = state.settings.voiceId,
                    onVoiceSelected = onVoiceSelected,
                )
        }
    }
}

@Composable
private fun AboutSection(
    versionName: String,
    onPrivacyPolicyClick: () -> Unit,
    onSourceRepositoryClick: () -> Unit,
    onOpenLicensesClick: () -> Unit,
) {
    SettingsSection(title = "About") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (versionName.isNotEmpty()) {
                Text(
                    text = "Version $versionName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            AboutLink(text = "Privacy policy", onClick = onPrivacyPolicyClick)
            AboutLink(text = "Open source licenses", onClick = onOpenLicensesClick)
            AboutLink(text = "Source code", onClick = onSourceRepositoryClick)
        }
    }
}

@Composable
private fun AboutLink(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}

@Composable
private fun HorizontalOptions(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        content()
    }
}

@Composable
private fun VoiceList(
    voices: List<VoiceOption>,
    selectedVoiceId: String?,
    onVoiceSelected: (String?) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(
            items = voices,
            key = { it.id },
        ) { voice ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RadioButton(
                    selected =
                        selectedVoiceId == voice.id ||
                            (selectedVoiceId == null && voice.id == "system"),
                    onClick = {
                        onVoiceSelected(if (voice.id == "system") null else voice.id)
                    },
                )
                Column {
                    val connectionLabel = if (voice.requiresNetwork) "Network" else "Local/system"

                    Text(voice.name)
                    Text(
                        text = "${voice.profile.displayName} - $connectionLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
