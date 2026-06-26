package com.abra.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listening_progress")
data class ListeningProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "ebook_id")
    val ebookId: String,
    @ColumnInfo(name = "segment_index") val segmentIndex: Int,
    @ColumnInfo(name = "character_offset") val characterOffset: Int,
    val completed: Boolean,
    @ColumnInfo(name = "updated_at_epoch_millis") val updatedAtEpochMillis: Long
)
