package com.music.cue.org.presentation.screens.songlist

import com.music.cue.org.domain.model.Song

data class SongListState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
