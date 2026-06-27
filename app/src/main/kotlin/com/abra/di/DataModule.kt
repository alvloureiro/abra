package com.abra.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.abra.data.local.AbraDatabase
import com.abra.data.local.dao.EbookDao
import com.abra.data.local.dao.ListeningProgressDao
import com.abra.data.local.dao.ListeningSegmentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AbraDatabase =
        Room
            .databaseBuilder(
                context,
                AbraDatabase::class.java,
                "abra.db",
            ).build()

    @Provides
    fun provideEbookDao(database: AbraDatabase): EbookDao = database.ebookDao()

    @Provides
    fun provideListeningProgressDao(database: AbraDatabase): ListeningProgressDao =
        database.listeningProgressDao()

    @Provides
    fun provideListeningSegmentDao(database: AbraDatabase): ListeningSegmentDao =
        database.listeningSegmentDao()

    @Provides
    @Singleton
    fun provideVoiceSettingsDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("voice_settings")
        }
}
