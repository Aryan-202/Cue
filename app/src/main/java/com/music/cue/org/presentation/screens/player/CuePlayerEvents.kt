package com.music.cue.org.presentation.screens.player

import com.music.cue.org.data.model.Track


interface CuePlayerEvents {

    fun onPlayPauseClick()

    fun onPreviousClick()

    fun onNextClick()

    fun onTrackClick(track: Track)

    fun onSeekBarPositionChanged(position: Long)
}