package com.abra.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.abra.presentation.library.LibraryRoute
import com.abra.presentation.reader.ReaderRoute
import com.abra.presentation.settings.SettingsRoute
import com.abra.presentation.ui.theme.AbraTheme

@Composable
fun AbraApp() {
    AbraTheme {
        var destination by rememberSaveable { mutableStateOf(MainDestination.Library) }
        var selectedEbookId by rememberSaveable { mutableStateOf<String?>(null) }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar {
                    MainDestination.entries.forEach { item ->
                        NavigationBarItem(
                            selected = destination == item,
                            onClick = { destination = item },
                            label = { Text(item.label) },
                            icon = { Text(item.iconLabel) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            when (destination) {
                MainDestination.Library -> LibraryRoute(
                    modifier = Modifier.padding(innerPadding),
                    onListen = { ebookId ->
                        selectedEbookId = ebookId
                        destination = MainDestination.Listen
                    }
                )

                MainDestination.Listen -> ReaderRoute(
                    ebookId = selectedEbookId,
                    modifier = Modifier.padding(innerPadding)
                )

                MainDestination.Settings -> SettingsRoute(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

private enum class MainDestination(
    val label: String,
    val iconLabel: String
) {
    Library("Library", "Books"),
    Listen("Listen", "Play"),
    Settings("Settings", "Voice")
}
