package com.music.cue.org.domain.usecase.song

import com.music.cue.org.domain.model.Song
import com.music.cue.org.domain.repository.ISongRepository
import kotlinx.coroutines.flow.Flow

class GetSongsUseCase(
    private val songsRepository: ISongRepository
) {
    operator fun invoke(): Flow<List<Song>> {
        return songsRepository.getSongs()
    }
}