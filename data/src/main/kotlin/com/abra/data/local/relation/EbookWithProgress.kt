package com.abra.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.abra.data.local.entity.EbookEntity
import com.abra.data.local.entity.ListeningProgressEntity

data class EbookWithProgress(
    @Embedded val ebook: EbookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "ebook_id",
    )
    val progress: ListeningProgressEntity?,
)
