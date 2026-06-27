package com.abra.di

import com.abra.data.pdf.PdfBoxPdfTextExtractor
import com.abra.data.repository.AndroidTtsVoiceCatalog
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
import com.abra.domain.repository.VoiceCatalog
import com.abra.domain.repository.VoiceSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindModule {
    @Binds
    @Singleton
    abstract fun bindEbookRepository(impl: DefaultEbookRepository): EbookRepository

    @Binds
    @Singleton
    abstract fun bindEbookContentRepository(
        impl: DefaultEbookContentRepository,
    ): EbookContentRepository

    @Binds
    @Singleton
    abstract fun bindListeningProgressRepository(
        impl: DefaultListeningProgressRepository,
    ): ListeningProgressRepository

    @Binds
    @Singleton
    abstract fun bindVoiceSettingsRepository(
        impl: DataStoreVoiceSettingsRepository,
    ): VoiceSettingsRepository

    @Binds
    @Singleton
    abstract fun bindVoiceCatalog(impl: AndroidTtsVoiceCatalog): VoiceCatalog

    @Binds
    @Singleton
    abstract fun bindPdfTextExtractor(impl: PdfBoxPdfTextExtractor): PdfTextExtractor

    @Binds
    @Singleton
    abstract fun bindAudioPlaybackEngine(
        impl: AndroidTextToSpeechPlaybackEngine,
    ): AudioPlaybackEngine
}
