package com.abra.di

import com.abra.domain.repository.AudioPlaybackEngine
import com.abra.domain.repository.EbookContentRepository
import com.abra.domain.repository.EbookRepository
import com.abra.domain.repository.ListeningProgressRepository
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
object UseCaseModule {
    @Provides
    fun provideImportEbookUseCase(repository: EbookRepository) = ImportEbookUseCase(repository)

    @Provides
    fun provideGetEbooksUseCase(repository: EbookRepository) = GetEbooksUseCase(repository)

    @Provides
    fun provideGetEbookUseCase(repository: EbookRepository) = GetEbookUseCase(repository)

    @Provides
    fun provideGetEbookTextUseCase(repository: EbookContentRepository) =
        GetEbookTextUseCase(repository)

    @Provides
    fun provideObservePlaybackStateUseCase(engine: AudioPlaybackEngine) =
        ObservePlaybackStateUseCase(engine)

    @Provides
    fun provideStartListeningUseCase(engine: AudioPlaybackEngine) =
        StartListeningUseCase(engine)

    @Provides
    fun providePauseListeningUseCase(engine: AudioPlaybackEngine) =
        PauseListeningUseCase(engine)

    @Provides
    fun provideResumeListeningUseCase(engine: AudioPlaybackEngine) =
        ResumeListeningUseCase(engine)

    @Provides
    fun provideStopListeningUseCase(engine: AudioPlaybackEngine) =
        StopListeningUseCase(engine)

    @Provides
    fun provideSkipListeningUseCase(engine: AudioPlaybackEngine) =
        SkipListeningUseCase(engine)

    @Provides
    fun provideObserveListeningProgressUseCase(repository: ListeningProgressRepository) =
        ObserveListeningProgressUseCase(repository)

    @Provides
    fun provideSaveListeningProgressUseCase(repository: ListeningProgressRepository) =
        SaveListeningProgressUseCase(repository)

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
    fun provideGetAvailableVoicesUseCase(repository: VoiceSettingsRepository) =
        GetAvailableVoicesUseCase(repository)
}
