package com.abra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.abra.data.local.entity.EbookEntity
import com.abra.data.local.relation.EbookWithProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface EbookDao {
    @Transaction
    @Query("SELECT * FROM ebooks ORDER BY imported_at_epoch_millis DESC")
    fun observeLibrary(): Flow<List<EbookWithProgress>>

    @Transaction
    @Query("SELECT * FROM ebooks WHERE id = :ebookId LIMIT 1")
    fun observeEbook(ebookId: String): Flow<EbookWithProgress?>

    @Transaction
    @Query("SELECT * FROM ebooks WHERE id = :ebookId LIMIT 1")
    suspend fun getEbookWithProgress(ebookId: String): EbookWithProgress?

    @Query("SELECT * FROM ebooks WHERE id = :ebookId LIMIT 1")
    suspend fun getEbook(ebookId: String): EbookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: EbookEntity)

    @Query(
        """
        UPDATE ebooks
        SET extraction_status = :status,
            extraction_message = :message,
            page_count = :pageCount
        WHERE id = :ebookId
        """
    )
    suspend fun updateExtraction(
        ebookId: String,
        status: String,
        message: String?,
        pageCount: Int
    )
}
