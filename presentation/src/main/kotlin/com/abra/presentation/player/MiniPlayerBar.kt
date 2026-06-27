package com.abra.presentation.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abra.domain.model.PlaybackStatus

@Composable
fun NowPlayingRoute(
    visible: Boolean,
    onExpand: () -> Unit,
    viewModel: NowPlayingViewModel = hiltViewModel(),
) {
    if (!visible) return

    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    MiniPlayerBar(
        state = state,
        onExpand = onExpand,
        onPause = viewModel::pause,
        onResume = viewModel::resume,
    )
}

@Composable
fun MiniPlayerBar(
    state: NowPlayingUiState,
    onExpand: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val playback = state.playbackState
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onExpand)
                .semantics { contentDescription = "Open full player" },
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp,
    ) {
        Column {
            LinearProgressIndicator(
                progress = { state.progress },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Mini player progress" },
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = state.ebookTitle ?: "Listening",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = state.segmentLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                MiniPlayerPlaybackButton(
                    status = playback.status,
                    onPause = onPause,
                    onResume = onResume,
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerPlaybackButton(
    status: PlaybackStatus,
    onPause: () -> Unit,
    onResume: () -> Unit,
) {
    when (status) {
        PlaybackStatus.PLAYING, PlaybackStatus.LOADING ->
            OutlinedButton(
                onClick = onPause,
                enabled = status == PlaybackStatus.PLAYING,
                modifier = Modifier.semantics { contentDescription = "Pause playback" },
            ) {
                Text("Pause")
            }

        PlaybackStatus.PAUSED ->
            OutlinedButton(
                onClick = onResume,
                modifier =
                    Modifier.semantics {
                        contentDescription = "Resume playback"
                    },
            ) {
                Text("Play")
            }

        else -> Unit
    }
}
