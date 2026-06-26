package com.abra.presentation.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abra.domain.model.PlaybackStatus

@Composable
fun ReaderRoute(
    ebookId: String?,
    modifier: Modifier = Modifier,
    viewModel: ReaderViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(ebookId) {
        if (ebookId != null) viewModel.openEbook(ebookId)
    }

    ReaderScreen(
        ebookId = ebookId,
        state = state,
        onPlay = viewModel::play,
        onPause = viewModel::pause,
        onResume = viewModel::resume,
        onStop = viewModel::stop,
        onSkipBackward = viewModel::skipBackward,
        onSkipForward = viewModel::skipForward,
        modifier = modifier
    )
}

@Composable
private fun ReaderScreen(
    ebookId: String?,
    state: ReaderUiState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Listen",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (ebookId == null) {
            EmptyReader()
            return@Column
        }

        if (state.isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        val ebook = state.ebook
        if (ebook == null) {
            Text(
                text = "Ebook was not found.",
                color = MaterialTheme.colorScheme.error
            )
            return@Column
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = ebook.metadata.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${state.segments.size} listening segments",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val progress = state.playbackProgress()
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Listening progress" }
        )
        Text(
            text = state.progressLabel(),
            style = MaterialTheme.typography.bodyMedium
        )

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        PlaybackControls(
            status = state.playbackState.status,
            canPlay = state.segments.isNotEmpty(),
            onPlay = onPlay,
            onPause = onPause,
            onResume = onResume,
            onStop = onStop,
            onSkipBackward = onSkipBackward,
            onSkipForward = onSkipForward
        )
    }
}

@Composable
private fun EmptyReader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Choose an ebook from Library.",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Your listening session will appear here.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PlaybackControls(
    status: PlaybackStatus,
    canPlay: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onSkipBackward,
            enabled = canPlay,
            modifier = Modifier.semantics { contentDescription = "Skip backward" }
        ) {
            Text("Back")
        }

        when (status) {
            PlaybackStatus.PLAYING -> Button(
                onClick = onPause,
                modifier = Modifier.semantics { contentDescription = "Pause playback" }
            ) {
                Text("Pause")
            }

            PlaybackStatus.PAUSED -> Button(
                onClick = onResume,
                modifier = Modifier.semantics { contentDescription = "Resume playback" }
            ) {
                Text("Resume")
            }

            else -> Button(
                onClick = onPlay,
                enabled = canPlay,
                modifier = Modifier.semantics { contentDescription = "Start playback" }
            ) {
                Text("Play")
            }
        }

        OutlinedButton(
            onClick = onStop,
            enabled = canPlay && status != PlaybackStatus.IDLE,
            modifier = Modifier.semantics { contentDescription = "Stop playback" }
        ) {
            Text("Stop")
        }

        OutlinedButton(
            onClick = onSkipForward,
            enabled = canPlay,
            modifier = Modifier.semantics { contentDescription = "Skip forward" }
        ) {
            Text("Next")
        }
    }
}

private fun ReaderUiState.playbackProgress(): Float {
    val total = playbackState.totalSegments.takeIf { it > 0 } ?: segments.size
    if (total <= 0) return 0f
    if (playbackState.status == PlaybackStatus.COMPLETED) return 1f
    val index = if (playbackState.ebookId == ebook?.id) {
        playbackState.segmentIndex
    } else {
        progress?.segmentIndex ?: 0
    }
    return ((index + 1).toFloat() / total).coerceIn(0f, 1f)
}

private fun ReaderUiState.progressLabel(): String {
    if (segments.isEmpty()) return "No extracted text available."
    val segment = if (playbackState.ebookId == ebook?.id) {
        playbackState.segmentIndex
    } else {
        progress?.segmentIndex ?: 0
    }.coerceIn(0, segments.lastIndex)
    return when (playbackState.status) {
        PlaybackStatus.PLAYING -> "Playing segment ${segment + 1} of ${segments.size}"
        PlaybackStatus.PAUSED -> "Paused at segment ${segment + 1} of ${segments.size}"
        PlaybackStatus.COMPLETED -> "Completed"
        PlaybackStatus.ERROR -> playbackState.message ?: "Playback error"
        else -> "Ready at segment ${segment + 1} of ${segments.size}"
    }
}
