package com.abra.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ebooks")
data class EbookEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "source_uri") val sourceUri: String,
    val title: String,
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "page_count") val pageCount: Int,
    @ColumnInfo(name = "imported_at_epoch_millis") val importedAtEpochMillis: Long,
    @ColumnInfo(name = "extraction_status") val extractionStatus: String,
    @ColumnInfo(name = "extraction_message") val extractionMessage: String?
)
