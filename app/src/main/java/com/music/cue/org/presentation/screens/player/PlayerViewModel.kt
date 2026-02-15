package com.music.cue.org.presentation.screens.player

import android.content.ContentUris
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.music.cue.org.domain.repository.ISongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val songRepository: ISongRepository
) : ViewModel() {

    private val songId = savedStateHandle.get<Long>("songId") ?: 0L

    private val _uiState = MutableStateFlow(PlayerUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    var exoPlayer: ExoPlayer? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                _uiState.update {
                    it.copy(
                        duration = exoPlayer?.duration ?: 0,
                        isLoading = false
                    )
                }
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            _uiState.update {
                it.copy(
                    duration = exoPlayer?.duration ?: 0
                )
            }
        }
    }

    init {
        loadSong()
    }

    private fun loadSong() {
        viewModelScope.launch {
            try {
                songRepository.getSongs().collect { songs ->
                    val song = songs.find { it.id == songId }
                    _uiState.update {
                        it.copy(
                            currentSong = song,
                            isLoading = false
                        )
                    }
                    syncPlayerWithSong()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to load song",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun syncPlayerWithSong() {
        val song = _uiState.value.currentSong
        val player = exoPlayer
        if (song != null && player != null) {
            val mediaItem = MediaItem.fromUri(song.data.toUri())
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }
    }

    fun initializePlayer(exoPlayer: ExoPlayer) {
        this.exoPlayer = exoPlayer
        exoPlayer.addListener(playerListener)
        syncPlayerWithSong()
    }

    fun releasePlayer() {
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
    }

    fun togglePlayPause() {
        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.pause()
        } else {
            exoPlayer?.play()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    private var positionUpdateJob: Job? = null
    
    fun updateCurrentPosition() {
        positionUpdateJob?.cancel()
        positionUpdateJob = viewModelScope.launch {
            while (true) {
                _uiState.update {
                    it.copy(currentPosition = exoPlayer?.currentPosition ?: 0)
                }
                delay(1000)
            }
        }
    }

    fun stopPositionUpdate() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    fun getAlbumArtUri(albumId: Long): Uri {
        return ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}