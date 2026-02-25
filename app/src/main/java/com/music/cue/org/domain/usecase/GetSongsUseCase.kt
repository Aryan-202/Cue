package com.music.cue.org.domain.usercase

import com.music.cue.org.data.model.Song
import com.music.cue.org.data.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(): Flow<List<Song>> = musicRepository.getAllSongs()
}