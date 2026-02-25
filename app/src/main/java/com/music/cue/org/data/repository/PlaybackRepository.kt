package com.music.cue.org.data.repository

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata // Added for metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer // Correct import
import androidx.media3.session.MediaSession
import com.music.cue.org.data.model.Song
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackRepository @Inject constructor(
    private val context: Context,
    private val exoPlayer: ExoPlayer,
    private val mediaSession: MediaSession
) {
    private var currentPlaylist: List<Song> = emptyList()
    private var currentIndex: Int = -1

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _shuffleMode = MutableStateFlow(false)
    val shuffleMode: StateFlow<Boolean> = _shuffleMode.asStateFlow()

    private var shuffledIndices: List<Int> = emptyList()

    // Coroutine scope for position updates
    private val repositoryScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    enum class PlaybackState { IDLE, BUFFERING, READY, ERROR }
    enum class RepeatMode { NONE, ONE, ALL }

    init {
        setupPlayerListeners()
        startPositionUpdates()
    }

    private fun setupPlayerListeners() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _playbackState.value = when (playbackState) {
                    Player.STATE_IDLE -> PlaybackState.IDLE
                    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
                    Player.STATE_READY -> PlaybackState.READY
                    Player.STATE_ENDED -> {
                        handlePlaybackEnd()
                        PlaybackState.IDLE
                    }
                    else -> PlaybackState.IDLE
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                updateCurrentSongFromPlayer()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateCurrentSongFromPlayer()
            }

            override fun onPlayerError(error: PlaybackException) {
                _playbackState.value = PlaybackState.ERROR
            }
        })
    }

    // Modern way to handle position updates without blocking threads
    private fun startPositionUpdates() {
        repositoryScope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    _currentPosition.value = exoPlayer.currentPosition
                    _duration.value = exoPlayer.duration
                }
                delay(500)
            }
        }
    }

    private fun handlePlaybackEnd() {
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                exoPlayer.seekTo(0)
                exoPlayer.play()
            }
            RepeatMode.ALL -> {
                skipToNext()
            }
            RepeatMode.NONE -> {
                if (hasNext()) {
                    skipToNext()
                } else {
                    exoPlayer.pause()
                }
            }
        }
    }

    private fun updateCurrentSongFromPlayer() {
        val currentMediaItem = exoPlayer.currentMediaItem
        if (currentMediaItem != null) {
            val songId = currentMediaItem.mediaId.toLongOrNull()
            songId?.let { id ->
                _currentSong.value = currentPlaylist.find { it.id == id }
                currentIndex = currentPlaylist.indexOfFirst { it.id == id }
            }
        }
    }

    fun setPlaylist(songs: List<Song>, startIndex: Int) {
        currentPlaylist = songs
        currentIndex = startIndex
        updateShuffledIndices()

        val mediaItems = songs.map { song ->
            // FIX: Use MediaMetadata.Builder for Title/Artist/Album
            val metadata = MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .build()

            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(Uri.parse(song.uri))
                .setMediaMetadata(metadata)
                .build()
        }

        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.seekTo(startIndex, 0L)
        exoPlayer.prepare()
    }

    fun play() = exoPlayer.play()

    fun pause() = exoPlayer.pause()

    fun playPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun skipToNext() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    fun skipToPrevious() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun setRepeatMode(mode: RepeatMode) {
        _repeatMode.value = mode
        exoPlayer.repeatMode = when (mode) {
            RepeatMode.NONE -> Player.REPEAT_MODE_OFF
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
        }
    }

    fun setShuffleMode(enabled: Boolean) {
        _shuffleMode.value = enabled
        exoPlayer.shuffleModeEnabled = enabled
    }

    // ExoPlayer has built-in logic for these, but keeping your logic if
    // you need custom shuffle behavior.
    fun hasNext(): Boolean = exoPlayer.hasNextMediaItem()
    fun hasPrevious(): Boolean = exoPlayer.hasPreviousMediaItem()

    private fun updateShuffledIndices() {
        if (_shuffleMode.value) {
            shuffledIndices = (currentPlaylist.indices).shuffled()
            if (currentIndex >= 0) {
                shuffledIndices = listOf(currentIndex) +
                        shuffledIndices.filter { it != currentIndex }
            }
        }
    }

    fun release() {
        repositoryScope.cancel() // Stop the position updates
        exoPlayer.release()
        mediaSession.release()
    }
}