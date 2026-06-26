package com.abra.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abra.data.local.dao.EbookDao
import com.abra.data.local.dao.ListeningProgressDao
import com.abra.data.local.dao.ListeningSegmentDao
import com.abra.data.local.entity.EbookEntity
import com.abra.data.local.entity.ListeningProgressEntity
import com.abra.data.local.entity.ListeningSegmentEntity

@Database(
    entities = [
        EbookEntity::class,
        ListeningProgressEntity::class,
        ListeningSegmentEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AbraDatabase : RoomDatabase() {
    abstract fun ebookDao(): EbookDao
    abstract fun listeningProgressDao(): ListeningProgressDao
    abstract fun listeningSegmentDao(): ListeningSegmentDao
}
