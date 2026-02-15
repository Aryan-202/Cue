package com.music.cue.org.domain.repository

import com.music.cue.org.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface IPlaylistRepository {
    fun getPlaylist(): Flow<List<Playlist>>
}