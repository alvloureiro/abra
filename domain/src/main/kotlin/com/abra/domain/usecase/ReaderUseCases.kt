package com.abra.domain.usecase

class ReaderQueryUseCases(
    val getEbook: GetEbookUseCase,
    val getEbookText: GetEbookTextUseCase,
    val observeVoiceSettings: ObserveVoiceSettingsUseCase,
)

class ReaderPlaybackUseCases(
    val observePlaybackState: ObservePlaybackStateUseCase,
    val startListening: StartListeningUseCase,
    val pauseListening: PauseListeningUseCase,
    val resumeListening: ResumeListeningUseCase,
    val stopListening: StopListeningUseCase,
    val skipListening: SkipListeningUseCase,
)

class ReaderProgressUseCases(
    val observeProgress: ObserveListeningProgressUseCase,
    val persistPlaybackProgress: PersistPlaybackProgressUseCase,
)
