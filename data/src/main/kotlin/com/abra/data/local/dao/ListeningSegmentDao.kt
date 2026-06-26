package com.abra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.abra.data.local.entity.ListeningSegmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ListeningSegmentDao {
    @Query("SELECT * FROM listening_segments WHERE ebook_id = :ebookId ORDER BY segment_index ASC")
    abstract fun observeSegments(ebookId: String): Flow<List<ListeningSegmentEntity>>

    @Query("DELETE FROM listening_segments WHERE ebook_id = :ebookId")
    abstract suspend fun deleteForEbook(ebookId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(segments: List<ListeningSegmentEntity>)

    @Transaction
    open suspend fun replaceSegments(
        ebookId: String,
        segments: List<ListeningSegmentEntity>,
    ) {
        deleteForEbook(ebookId)
        insertAll(segments)
    }
}
