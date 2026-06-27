package com.abra.di

import com.abra.domain.repository.AudioPlaybackEngine
import com.abra.domain.repository.EbookContentRepository
import com.abra.domain.repository.EbookRepository
import com.abra.domain.repository.ListeningProgressRepository
import com.abra.domain.repository.PdfTextExtractor
import com.abra.domain.repository.VoiceCatalog
import com.abra.domain.repository.VoiceSettingsRepository
import com.abra.domain.usecase.GetAvailableVoicesUseCase
import com.abra.domain.usecase.GetEbookTextUseCase
import com.abra.domain.usecase.GetEbookUseCase
import com.abra.domain.usecase.GetEbooksUseCase
import com.abra.domain.usecase.ImportEbookUseCase
import com.abra.domain.usecase.ObserveListeningProgressUseCase
import com.abra.domain.usecase.ObservePlaybackStateUseCase
import com.abra.domain.usecase.ObserveVoiceSettingsUseCase
import com.abra.domain.usecase.PauseListeningUseCase
import com.abra.domain.usecase.PersistPlaybackProgressUseCase
import com.abra.domain.usecase.NowPlayingUseCases
import com.abra.domain.usecase.ReaderPlaybackUseCases
import com.abra.domain.usecase.ReaderProgressUseCases
import com.abra.domain.usecase.ReaderQueryUseCases
import com.abra.domain.usecase.RefreshEbookTextUseCase
import com.abra.domain.usecase.ResumeListeningUseCase
import com.abra.domain.usecase.SaveListeningProgressUseCase
import com.abra.domain.usecase.SkipListeningUseCase
import com.abra.domain.usecase.StartListeningUseCase
import com.abra.domain.usecase.StopListeningUseCase
import com.abra.domain.usecase.UpdateVoiceIdUseCase
import com.abra.domain.usecase.UpdateVoiceLanguageUseCase
import com.abra.domain.usecase.UpdateVoiceProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object EbookUseCaseModule {
    @Provides
    fun provideImportEbookUseCase(
        repository: EbookRepository,
        pdfTextExtractor: PdfTextExtractor,
        ebookContentRepository: EbookContentRepository,
    ) = ImportEbookUseCase(repository, pdfTextExtractor, ebookContentRepository)

    @Provides
    fun provideRefreshEbookTextUseCase(ebookContentRepository: EbookContentRepository) =
        RefreshEbookTextUseCase(ebookContentRepository)

    @Provides
    fun provideGetEbooksUseCase(repository: EbookRepository) = GetEbooksUseCase(repository)

    @Provides
    fun provideGetEbookUseCase(repository: EbookRepository) = GetEbookUseCase(repository)

    @Provides
    fun provideGetEbookTextUseCase(repository: EbookContentRepository) =
        GetEbookTextUseCase(repository)
}

@Module
@InstallIn(SingletonComponent::class)
object PlaybackUseCaseModule {
    @Provides
    fun provideObservePlaybackStateUseCase(engine: AudioPlaybackEngine) =
        ObservePlaybackStateUseCase(engine)

    @Provides
    fun provideStartListeningUseCase(engine: AudioPlaybackEngine) = StartListeningUseCase(engine)

    @Provides
    fun providePauseListeningUseCase(engine: AudioPlaybackEngine) = PauseListeningUseCase(engine)

    @Provides
    fun provideResumeListeningUseCase(engine: AudioPlaybackEngine) = ResumeListeningUseCase(engine)

    @Provides
    fun provideStopListeningUseCase(engine: AudioPlaybackEngine) = StopListeningUseCase(engine)

    @Provides
    fun provideSkipListeningUseCase(engine: AudioPlaybackEngine) = SkipListeningUseCase(engine)

    @Provides
    fun provideObserveListeningProgressUseCase(repository: ListeningProgressRepository) =
        ObserveListeningProgressUseCase(repository)

    @Provides
    fun provideSaveListeningProgressUseCase(repository: ListeningProgressRepository) =
        SaveListeningProgressUseCase(repository)

    @Provides
    fun providePersistPlaybackProgressUseCase(
        saveListeningProgress: SaveListeningProgressUseCase,
    ) = PersistPlaybackProgressUseCase(saveListeningProgress)
}

@Module
@InstallIn(SingletonComponent::class)
object VoiceSettingsUseCaseModule {
    @Provides
    fun provideObserveVoiceSettingsUseCase(repository: VoiceSettingsRepository) =
        ObserveVoiceSettingsUseCase(repository)

    @Provides
    fun provideUpdateVoiceLanguageUseCase(repository: VoiceSettingsRepository) =
        UpdateVoiceLanguageUseCase(repository)

    @Provides
    fun provideUpdateVoiceProfileUseCase(repository: VoiceSettingsRepository) =
        UpdateVoiceProfileUseCase(repository)

    @Provides
    fun provideUpdateVoiceIdUseCase(repository: VoiceSettingsRepository) =
        UpdateVoiceIdUseCase(repository)

    @Provides
    fun provideGetAvailableVoicesUseCase(voiceCatalog: VoiceCatalog) =
        GetAvailableVoicesUseCase(voiceCatalog)
}

@Module
@InstallIn(SingletonComponent::class)
object ReaderUseCaseModule {
    @Provides
    fun provideReaderQueryUseCases(
        getEbook: GetEbookUseCase,
        getEbookText: GetEbookTextUseCase,
        observeVoiceSettings: ObserveVoiceSettingsUseCase,
    ) = ReaderQueryUseCases(getEbook, getEbookText, observeVoiceSettings)

    @Provides
    fun provideReaderPlaybackUseCases(
        observePlaybackState: ObservePlaybackStateUseCase,
        startListening: StartListeningUseCase,
        pauseListening: PauseListeningUseCase,
        resumeListening: ResumeListeningUseCase,
        stopListening: StopListeningUseCase,
        skipListening: SkipListeningUseCase,
    ) = ReaderPlaybackUseCases(
        observePlaybackState,
        startListening,
        pauseListening,
        resumeListening,
        stopListening,
        skipListening,
    )

    @Provides
    fun provideReaderProgressUseCases(
        observeProgress: ObserveListeningProgressUseCase,
        persistPlaybackProgress: PersistPlaybackProgressUseCase,
    ) = ReaderProgressUseCases(observeProgress, persistPlaybackProgress)

    @Provides
    fun provideNowPlayingUseCases(
        observePlaybackState: ObservePlaybackStateUseCase,
        getEbook: GetEbookUseCase,
        pauseListening: PauseListeningUseCase,
        resumeListening: ResumeListeningUseCase,
    ) = NowPlayingUseCases(
        observePlaybackState,
        getEbook,
        pauseListening,
        resumeListening,
    )
}
