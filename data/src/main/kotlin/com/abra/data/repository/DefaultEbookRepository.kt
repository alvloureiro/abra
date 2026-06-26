package com.abra.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import com.abra.data.local.dao.EbookDao
import com.abra.data.local.dao.ListeningSegmentDao
import com.abra.data.local.entity.EbookEntity
import com.abra.data.local.mapper.toDomain
import com.abra.data.local.mapper.toEntity
import com.abra.domain.model.Ebook
import com.abra.domain.model.EbookExtractionStatus
import com.abra.domain.model.PdfExtractionResult
import com.abra.domain.repository.EbookRepository
import com.abra.domain.repository.PdfTextExtractor
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DefaultEbookRepository(
    private val context: Context,
    private val ebookDao: EbookDao,
    private val listeningSegmentDao: ListeningSegmentDao,
    private val pdfTextExtractor: PdfTextExtractor
) : EbookRepository {
    override fun observeLibrary(): Flow<List<Ebook>> {
        return ebookDao.observeLibrary().map { ebooks ->
            ebooks.map { it.toDomain() }
        }
    }

    override fun observeEbook(ebookId: String): Flow<Ebook?> {
        return ebookDao.observeEbook(ebookId).map { it?.toDomain() }
    }

    override suspend fun importEbook(sourceUri: String): Ebook = withContext(Dispatchers.IO) {
        val ebookId = UUID.randomUUID().toString()
        val uri = Uri.parse(sourceUri)
        persistReadPermission(uri)

        val fileName = resolveDisplayName(uri) ?: "Imported ebook.pdf"
        val pendingEntity = EbookEntity(
            id = ebookId,
            sourceUri = sourceUri,
            title = fileName.removeSuffix(".pdf").ifBlank { fileName },
            fileName = fileName,
            pageCount = 0,
            importedAtEpochMillis = System.currentTimeMillis(),
            extractionStatus = EbookExtractionStatus.PENDING.name,
            extractionMessage = null
        )
        ebookDao.upsert(pendingEntity)

        when (val result = pdfTextExtractor.extract(ebookId, sourceUri)) {
            is PdfExtractionResult.Success -> {
                listeningSegmentDao.replaceSegments(
                    ebookId = ebookId,
                    segments = result.segments.map { it.toEntity() }
                )
                ebookDao.updateExtraction(
                    ebookId = ebookId,
                    status = EbookExtractionStatus.READY.name,
                    message = null,
                    pageCount = result.pageCount
                )
            }

            is PdfExtractionResult.Unsupported -> {
                ebookDao.updateExtraction(
                    ebookId = ebookId,
                    status = EbookExtractionStatus.UNSUPPORTED.name,
                    message = result.reason,
                    pageCount = result.pageCount
                )
            }

            is PdfExtractionResult.Failure -> {
                ebookDao.updateExtraction(
                    ebookId = ebookId,
                    status = EbookExtractionStatus.FAILED.name,
                    message = result.message,
                    pageCount = 0
                )
            }
        }

        checkNotNull(ebookDao.getEbookWithProgress(ebookId)) {
            "Imported ebook could not be reloaded."
        }.toDomain()
    }

    private fun persistReadPermission(uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    private fun resolveDisplayName(uri: Uri): String? {
        return context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index < 0) null else cursor.getString(index)
        }
    }
}
