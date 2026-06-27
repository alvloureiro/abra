package com.abra.domain.usecase

class NowPlayingUseCases(
    val observePlaybackState: ObservePlaybackStateUseCase,
    val getEbook: GetEbookUseCase,
    val pauseListening: PauseListeningUseCase,
    val resumeListening: ResumeListeningUseCase,
)
