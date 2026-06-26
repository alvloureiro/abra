package com.abra.data.repository

import com.abra.data.local.dao.ListeningProgressDao
import com.abra.data.local.mapper.toDomain
import com.abra.data.local.mapper.toEntity
import com.abra.domain.model.ListeningProgress
import com.abra.domain.repository.ListeningProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultListeningProgressRepository(
    private val listeningProgressDao: ListeningProgressDao,
) : ListeningProgressRepository {
    override fun observeProgress(ebookId: String): Flow<ListeningProgress?> =
        listeningProgressDao.observeProgress(ebookId).map { it?.toDomain() }

    override suspend fun saveProgress(progress: ListeningProgress) {
        listeningProgressDao.upsert(progress.toEntity())
    }
}
