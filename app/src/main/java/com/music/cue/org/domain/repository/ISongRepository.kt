package com.music.cue.org.domain.repository

import com.music.cue.org.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface ISongRepository {
    fun getSongs(): Flow<List<Song>>
}