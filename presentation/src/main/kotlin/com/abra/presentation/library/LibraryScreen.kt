package com.abra.presentation.library

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.abra.domain.model.Ebook
import com.abra.domain.model.EbookExtractionStatus

@Composable
fun LibraryRoute(
    onListen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val importLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = { uri ->
                if (uri != null) viewModel.importPdf(uri.toString())
            },
        )

    LibraryScreen(
        state = state,
        onImport = { importLauncher.launch(arrayOf("application/pdf")) },
        onListen = onListen,
        modifier = modifier,
    )
}

@Composable
private fun LibraryScreen(
    state: LibraryUiState,
    onImport: () -> Unit,
    onListen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Abra",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "PDF audio ebooks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = onImport,
                enabled = !state.isImporting,
                modifier =
                    Modifier.semantics {
                        contentDescription = "Import PDF ebook"
                    },
            ) {
                Text(if (state.isImporting) "Importing" else "Add PDF")
            }
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        when {
            state.isLoading -> CircularProgressIndicator()
            state.ebooks.isEmpty() -> EmptyLibrary(onImport = onImport)
            else ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = state.ebooks,
                        key = { it.id },
                    ) { ebook ->
                        EbookRow(
                            ebook = ebook,
                            onListen = { onListen(ebook.id) },
                        )
                    }
                }
        }
    }
}

@Composable
private fun EmptyLibrary(onImport: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "No ebooks yet",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "Add a text-based PDF to start listening.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(onClick = onImport) {
            Text("Choose PDF")
        }
    }
}

@Composable
private fun EbookRow(
    ebook: Ebook,
    onListen: () -> Unit,
) {
    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = ebook.metadata.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = ebook.metadata.fileName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = ebook.statusLabel(),
                style = MaterialTheme.typography.bodyMedium,
                color =
                    when (ebook.extractionStatus) {
                        EbookExtractionStatus.READY -> MaterialTheme.colorScheme.primary
                        EbookExtractionStatus.UNSUPPORTED,
                        EbookExtractionStatus.FAILED,
                        -> MaterialTheme.colorScheme.error
                        EbookExtractionStatus.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )

            Spacer(modifier = Modifier.height(2.dp))

            Button(
                onClick = onListen,
                enabled = ebook.extractionStatus == EbookExtractionStatus.READY,
                modifier =
                    Modifier.semantics {
                        contentDescription = "Listen to ${ebook.metadata.title}"
                    },
            ) {
                Text("Listen")
            }
        }
    }
}

private fun Ebook.statusLabel(): String =
    when (extractionStatus) {
        EbookExtractionStatus.PENDING -> "Extracting text"
        EbookExtractionStatus.READY ->
            progress?.let { savedProgress ->
                val segmentNumber = savedProgress.segmentIndex + 1
                if (savedProgress.completed) {
                    "Completed"
                } else {
                    "Progress saved at segment $segmentNumber"
                }
            } ?: "Ready to listen"
        EbookExtractionStatus.UNSUPPORTED -> extractionMessage ?: "Unsupported PDF"
        EbookExtractionStatus.FAILED -> extractionMessage ?: "Import failed"
    }
