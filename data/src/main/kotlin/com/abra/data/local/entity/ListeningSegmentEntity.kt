package com.abra.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "listening_segments",
    primaryKeys = ["ebook_id", "segment_index"]
)
data class ListeningSegmentEntity(
    @ColumnInfo(name = "ebook_id") val ebookId: String,
    @ColumnInfo(name = "segment_index") val segmentIndex: Int,
    @ColumnInfo(name = "page_number") val pageNumber: Int,
    val text: String
)
