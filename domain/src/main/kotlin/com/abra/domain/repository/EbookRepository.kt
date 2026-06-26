package com.abra.domain.repository

import com.abra.domain.model.Ebook
import kotlinx.coroutines.flow.Flow

interface EbookRepository {
    fun observeLibrary(): Flow<List<Ebook>>

    fun observeEbook(ebookId: String): Flow<Ebook?>

    suspend fun importEbook(sourceUri: String): Ebook
}
