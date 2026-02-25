package com.music.cue.org.presentation.screens.player


import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class CuePlayer @Inject constructor(private val exoPlayer: ExoPlayer): Player.Listener {

    val playerState = MutableStateFlow(CuePlayerState.STATE_IDLE)

    val currentPosition: Long
        get() = if (exoPlayer.currentPosition > 0) exoPlayer.currentPosition else 0L

    val currentDuration: Long
        get() = if (exoPlayer.duration > 0) exoPlayer.duration else 0L

    val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    fun initializePlayer (trackList: MutableList<MediaItem>) {
        exoPlayer.addListener(this)
        exoPlayer.setMediaItems(trackList)
        exoPlayer.prepare()
    }

    fun setUpTrack (index: Int, isTrackPlaying: Boolean) {
        if (exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare()
        exoPlayer.seekTo(index, 0)
        if (isTrackPlaying) exoPlayer.playWhenReady = true
    }

    fun playPause () {
        if (exoPlayer.playbackState == Player.STATE_IDLE) exoPlayer.prepare()
        exoPlayer.playWhenReady = !exoPlayer.playWhenReady
    }

    fun releasePlayer() {
        exoPlayer.release()
    }

    fun seekToPosition (position: Long) {
        exoPlayer.seekTo(position)
    }

//    override fun onPlayerError(error: PlaybackException?) {
//        super.onPlayerError(error)
//        playerState.tryEmit(CuePlayerState.STATE_ERROR)
//    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        if (playWhenReady) {
            playerState.tryEmit(CuePlayerState.STATE_PLAYING)
        } else {
            playerState.tryEmit(CuePlayerState.STATE_PAUSE)
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
            playerState.tryEmit(CuePlayerState.STATE_NEXT_TRACK)
            playerState.tryEmit(CuePlayerState.STATE_PLAYING)
        }
    }

    fun getMediaItemCount(): Int {
        return exoPlayer.mediaItemCount
    }

    fun getCurrentMediaItemIndex(): Int {
        return exoPlayer.currentMediaItemIndex
    }


    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            Player.STATE_IDLE -> {
                playerState.tryEmit(CuePlayerState.STATE_IDLE)
            }

            Player.STATE_BUFFERING -> {
                playerState.tryEmit(CuePlayerState.STATE_BUFFERING)
            }

            Player.STATE_READY -> {
                playerState.tryEmit(CuePlayerState.STATE_READY)
                if (exoPlayer.playWhenReady) {
                    playerState.tryEmit(CuePlayerState.STATE_PLAYING)
                } else {
                    playerState.tryEmit(CuePlayerState.STATE_PAUSE)
                }
            }

            Player.STATE_ENDED -> {
                playerState.tryEmit(CuePlayerState.STATE_END)
            }
        }
    }
}