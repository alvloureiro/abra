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
import com.abra.data.pdf.PdfBoxPdfTextExtractor
import com.abra.data.repository.DataStoreVoiceSettingsRepository
import com.abra.data.repository.DefaultEbookContentRepository
import com.abra.data.repository.DefaultEbookRepository
import com.abra.data.repository.DefaultListeningProgressRepository
import com.abra.data.tts.AndroidTextToSpeechPlaybackEngine
import com.abra.domain.repository.AudioPlaybackEngine
import com.abra.domain.repository.EbookContentRepository
import com.abra.domain.repository.EbookRepository
import com.abra.domain.repository.ListeningProgressRepository
import com.abra.domain.repository.PdfTextExtractor
import com.abra.domain.repository.VoiceSettingsRepository
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

    @Provides
    @Singleton
    fun providePdfTextExtractor(
        @ApplicationContext context: Context,
    ): PdfTextExtractor = PdfBoxPdfTextExtractor(context)

    @Provides
    @Singleton
    fun provideEbookRepository(
        @ApplicationContext context: Context,
        ebookDao: EbookDao,
        listeningSegmentDao: ListeningSegmentDao,
        pdfTextExtractor: PdfTextExtractor,
    ): EbookRepository =
        DefaultEbookRepository(
            context = context,
            ebookDao = ebookDao,
            listeningSegmentDao = listeningSegmentDao,
            pdfTextExtractor = pdfTextExtractor,
        )

    @Provides
    @Singleton
    fun provideEbookContentRepository(
        ebookDao: EbookDao,
        listeningSegmentDao: ListeningSegmentDao,
        pdfTextExtractor: PdfTextExtractor,
    ): EbookContentRepository =
        DefaultEbookContentRepository(
            ebookDao = ebookDao,
            listeningSegmentDao = listeningSegmentDao,
            pdfTextExtractor = pdfTextExtractor,
        )

    @Provides
    @Singleton
    fun provideListeningProgressRepository(
        listeningProgressDao: ListeningProgressDao,
    ): ListeningProgressRepository = DefaultListeningProgressRepository(listeningProgressDao)

    @Provides
    @Singleton
    fun provideVoiceSettingsRepository(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>,
    ): VoiceSettingsRepository =
        DataStoreVoiceSettingsRepository(
            context = context,
            dataStore = dataStore,
        )

    @Provides
    @Singleton
    fun provideAudioPlaybackEngine(
        @ApplicationContext context: Context,
    ): AudioPlaybackEngine = AndroidTextToSpeechPlaybackEngine(context)
}
