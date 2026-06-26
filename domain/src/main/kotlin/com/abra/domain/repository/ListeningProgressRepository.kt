package com.abra.domain.repository

import com.abra.domain.model.ListeningProgress
import kotlinx.coroutines.flow.Flow

interface ListeningProgressRepository {
    fun observeProgress(ebookId: String): Flow<ListeningProgress?>
    suspend fun saveProgress(progress: ListeningProgress)
}
