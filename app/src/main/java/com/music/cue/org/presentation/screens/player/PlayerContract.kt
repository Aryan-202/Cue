package com.music.cue.org.presentation.screens.player

import com.music.cue.org.domain.model.Song

data class PlayerUiState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)