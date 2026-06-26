package com.abra.domain.repository

import com.abra.domain.model.Ebook
import kotlinx.coroutines.flow.Flow

interface EbookRepository {
    fun observeLibrary(): Flow<List<Ebook>>

    fun observeEbook(ebookId: String): Flow<Ebook?>

    suspend fun createPendingImport(sourceUri: String): Ebook

    suspend fun requireEbook(ebookId: String): Ebook
}
