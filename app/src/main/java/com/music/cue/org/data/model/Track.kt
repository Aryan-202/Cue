package com.music.cue.org.data.model


import com.music.cue.org.presentation.screens.player.CuePlayerState

data class Track(
    val trackId: Int = 0,
    val trackName: String = "",
    val trackUrl: String = "",
    val trackImage: Int = 0,
    val artistName: String = "",
    var isSelected: Boolean = false,
    var state: CuePlayerState = CuePlayerState.STATE_IDLE
){
    class Builder {
        private var trackId: Int = 0
        private lateinit var trackName: String
        private lateinit var trackUrl: String
        private var trackImage: Int = 0
        private lateinit var artistName: String
        private var isSelected: Boolean = false
        private var state: CuePlayerState = CuePlayerState.STATE_IDLE

        fun trackId(trackId: Int) = apply { this.trackId = trackId }
        fun trackName(trackName: String) = apply { this.trackName = trackName }
        fun trackUrl(trackUrl: String) = apply { this.trackUrl = trackUrl }
        fun trackImage(trackImage: Int) = apply { this.trackImage = trackImage }
        fun artistName(artistName: String) = apply { this.artistName = artistName }
    }
}

