package com.abra.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.abra.data.local.dao.EbookDao
import com.abra.data.local.entity.EbookEntity
import com.abra.data.local.mapper.toDomain
import com.abra.domain.model.Ebook
import com.abra.domain.model.EbookExtractionStatus
import com.abra.domain.repository.EbookRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class DefaultEbookRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val ebookDao: EbookDao,
    ) : EbookRepository {
    override fun observeLibrary(): Flow<List<Ebook>> =
        ebookDao.observeLibrary().map { ebooks ->
            ebooks.map { it.toDomain() }
        }

    override fun observeEbook(ebookId: String): Flow<Ebook?> =
        ebookDao.observeEbook(ebookId).map {
            it?.toDomain()
        }

    override suspend fun createPendingImport(sourceUri: String): Ebook =
        withContext(Dispatchers.IO) {
            val ebookId = UUID.randomUUID().toString()
            val uri = sourceUri.toUri()
            persistReadPermission(uri)

            val fileName = resolveDisplayName(uri) ?: "Imported ebook.pdf"
            val pendingEntity =
                EbookEntity(
                    id = ebookId,
                    sourceUri = sourceUri,
                    title = fileName.removeSuffix(".pdf").ifBlank { fileName },
                    fileName = fileName,
                    pageCount = 0,
                    importedAtEpochMillis = System.currentTimeMillis(),
                    extractionStatus = EbookExtractionStatus.PENDING.name,
                    extractionMessage = null,
                )
            ebookDao.upsert(pendingEntity)
            pendingEntity.toDomain()
        }

    override suspend fun requireEbook(ebookId: String): Ebook =
        checkNotNull(ebookDao.getEbookWithProgress(ebookId)?.toDomain()) {
            "Ebook $ebookId could not be loaded."
        }

    private fun persistReadPermission(uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
    }

    private fun resolveDisplayName(uri: Uri): String? {
        val cursor =
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null,
            ) ?: return null
        return cursor.use {
            if (!it.moveToFirst()) return@use null
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index < 0) null else it.getString(index)
        }
    }
}
