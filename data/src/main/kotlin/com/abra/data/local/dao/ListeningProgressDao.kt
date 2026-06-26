package com.abra.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abra.data.local.entity.ListeningProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListeningProgressDao {
    @Query("SELECT * FROM listening_progress WHERE ebook_id = :ebookId LIMIT 1")
    fun observeProgress(ebookId: String): Flow<ListeningProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ListeningProgressEntity)
}
