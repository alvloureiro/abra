package com.abra.presentation.reader

import com.abra.domain.usecase.GetEbookTextUseCase
import com.abra.domain.usecase.GetEbookUseCase
import com.abra.domain.usecase.ObserveListeningProgressUseCase
import com.abra.domain.usecase.ObservePlaybackStateUseCase
import com.abra.domain.usecase.ObserveVoiceSettingsUseCase
import com.abra.domain.usecase.PauseListeningUseCase
import com.abra.domain.usecase.ResumeListeningUseCase
import com.abra.domain.usecase.SaveListeningProgressUseCase
import com.abra.domain.usecase.SkipListeningUseCase
import com.abra.domain.usecase.StartListeningUseCase
import com.abra.domain.usecase.StopListeningUseCase
import javax.inject.Inject

class ReaderQueryUseCases
    @Inject
    constructor(
        val getEbook: GetEbookUseCase,
        val getEbookText: GetEbookTextUseCase,
        val observeVoiceSettings: ObserveVoiceSettingsUseCase,
    )

class ReaderPlaybackUseCases
    @Inject
    constructor(
        val observePlaybackState: ObservePlaybackStateUseCase,
        val startListening: StartListeningUseCase,
        val pauseListening: PauseListeningUseCase,
        val resumeListening: ResumeListeningUseCase,
        val stopListening: StopListeningUseCase,
        val skipListening: SkipListeningUseCase,
    )

class ReaderProgressUseCases
    @Inject
    constructor(
        val observeProgress: ObserveListeningProgressUseCase,
        val saveProgress: SaveListeningProgressUseCase,
    )
