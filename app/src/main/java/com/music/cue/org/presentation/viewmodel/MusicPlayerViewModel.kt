package com.music.cue.org.presentation.viewmodel

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.music.cue.org.data.model.Album
import com.music.cue.org.data.model.Artist
import com.music.cue.org.data.model.Playlist
import com.music.cue.org.data.model.Song
import com.music.cue.org.data.repository.MusicRepository
import com.music.cue.org.presentation.screens.player.CuePlayer
import com.music.cue.org.presentation.screens.player.CuePlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val cuePlayer: CuePlayer,
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicPlayerUiState())
    val uiState: StateFlow<MusicPlayerUiState> = _uiState.asStateFlow()

    val currentSong: StateFlow<Song?> = _uiState.map { it.currentSong }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)
    val isPlaying: StateFlow<Boolean> = _uiState.map { it.isPlaying }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)
    val playerState: StateFlow<CuePlayerState> = _uiState.map { it.playerState }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), CuePlayerState.STATE_IDLE)
    val currentPosition: StateFlow<Long> = _uiState.map { it.currentPosition }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0L)
    val currentDuration: StateFlow<Long> = _uiState.map { it.currentDuration }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0L)
    val isBuffering: StateFlow<Boolean> = _uiState.map { it.isBuffering }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)
    val currentSongIndex: StateFlow<Int> = _uiState.map { it.currentSongIndex }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)
    
    val songs: StateFlow<List<Song>> = musicRepository.getAllSongs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteSongs: StateFlow<List<Song>> = musicRepository.getFavoriteSongs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyPlayed: StateFlow<List<Song>> = musicRepository.getRecentlyPlayedIds()
        .map { ids -> ids.mapNotNull { musicRepository.getSongById(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private var currentPlaylist: List<Song> = emptyList()

    init {
        observePlayerState()
        observePositionUpdates()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            cuePlayer.playerState.collect { state ->
                when (state) {
                    CuePlayerState.STATE_PLAYING -> {
                        _uiState.update {
                            it.copy(
                                playerState = state,
                                isPlaying = true,
                                isBuffering = false
                            )
                        }
                    }

                    CuePlayerState.STATE_PAUSE -> {
                        _uiState.update {
                            it.copy(
                                playerState = state,
                                isPlaying = false,
                                isBuffering = false
                            )
                        }
                    }

                    CuePlayerState.STATE_BUFFERING -> {
                        _uiState.update {
                            it.copy(
                                playerState = state,
                                isBuffering = true
                            )
                        }
                    }

                    CuePlayerState.STATE_NEXT_TRACK -> {
                        // ExoPlayer auto-advanced — sync current song from player index
                        val newIndex = cuePlayer.getCurrentMediaItemIndex()
                        val newSong = currentPlaylist.getOrNull(newIndex)
                        _uiState.update {
                            it.copy(
                                playerState = state,
                                currentSong = newSong,
                                currentSongIndex = newIndex
                            )
                        }
                    }

                    CuePlayerState.STATE_END -> {
                        _uiState.update {
                            it.copy(
                                playerState = state,
                                isPlaying = false,
                                currentPosition = 0L
                            )
                        }
                    }

                    CuePlayerState.STATE_IDLE -> {
                        _uiState.update {
                            it.copy(
                                playerState = state,
                                isPlaying = false,
                                isBuffering = false
                            )
                        }
                    }

                    CuePlayerState.STATE_READY -> {
                        _uiState.update {
                            it.copy(
                                playerState = state,
                                isBuffering = false
                            )
                        }
                    }

                    else -> {
                        _uiState.update { it.copy(playerState = state) }
                    }
                }
            }
        }
    }

    private fun observePositionUpdates() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                _uiState.update {
                    it.copy(
                        currentPosition = cuePlayer.currentPosition,
                        currentDuration = cuePlayer.currentDuration,
                        isPlaying = cuePlayer.isPlaying
                    )
                }
            }
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        currentPlaylist = songs

        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setUri(song.uri)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setArtworkUri(song.albumArtUri?.toUri())
                        .build()
                )
                .build()
        }.toMutableList()

        // Re-initialize only when playlist changes or item count differs
        if (mediaItems.size != cuePlayer.getMediaItemCount()) {
            cuePlayer.initializePlayer(mediaItems)
        }

        cuePlayer.setUpTrack(startIndex, true)

        _uiState.update {
            it.copy(
                currentSong = songs.getOrNull(startIndex),
                currentSongIndex = startIndex,
                currentPlaylist = songs,
                isPlaying = true
            )
        }
    }

    fun playPause() {
        cuePlayer.playPause()
        // State will update via playerState observer
    }

    fun playNext() {
        val currentIndex = getCurrentSongIndex()
        if (currentIndex < currentPlaylist.size - 1) {
            val nextIndex = currentIndex + 1
            cuePlayer.setUpTrack(nextIndex, cuePlayer.isPlaying)
            _uiState.update {
                it.copy(
                    currentSong = currentPlaylist.getOrNull(nextIndex),
                    currentSongIndex = nextIndex
                )
            }
        }
    }

    fun playPrevious() {
        val currentIndex = getCurrentSongIndex()
        // If more than 3s in, restart current track; otherwise go to previous
        if (cuePlayer.currentPosition > 3000L) {
            cuePlayer.seekToPosition(0L)
        } else if (currentIndex > 0) {
            val prevIndex = currentIndex - 1
            cuePlayer.setUpTrack(prevIndex, cuePlayer.isPlaying)
            _uiState.update {
                it.copy(
                    currentSong = currentPlaylist.getOrNull(prevIndex),
                    currentSongIndex = prevIndex
                )
            }
        }
    }

    fun seekTo(position: Long) {
        cuePlayer.seekToPosition(position)
        _uiState.update { it.copy(currentPosition = position) }
    }

    fun toggleFavorite(song: Song) {
        val updatedSong = song.copy(isFavorite = !song.isFavorite)
        val updatedPlaylist = currentPlaylist.map {
            if (it.id == song.id) updatedSong else it
        }
        currentPlaylist = updatedPlaylist

        if (_uiState.value.currentSong?.id == song.id) {
            _uiState.update {
                it.copy(
                    currentSong = updatedSong,
                    currentPlaylist = updatedPlaylist
                )
            }
        } else {
            _uiState.update { it.copy(currentPlaylist = updatedPlaylist) }
        }
    }

    fun getCurrentSongProgress(): Float {
        val duration = cuePlayer.currentDuration
        if (duration == 0L) return 0f
        return cuePlayer.currentPosition.toFloat() / duration.toFloat()
    }

    fun getMediaItemCount(): Int = cuePlayer.getMediaItemCount()

    fun getCurrentMediaItemIndex(): Int = cuePlayer.getCurrentMediaItemIndex()

    private fun getCurrentSongIndex(): Int {
        return currentPlaylist.indexOfFirst { it.id == _uiState.value.currentSong?.id }
    }

    override fun onCleared() {
        super.onCleared()
        cuePlayer.releasePlayer()
    }
}

data class MusicPlayerUiState(
    val currentSong: Song? = null,
    val currentPlaylist: List<Song> = emptyList(),
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val playerState: CuePlayerState = CuePlayerState.STATE_IDLE,
    val currentPosition: Long = 0L,
    val currentDuration: Long = 0L,
    val currentSongIndex: Int = 0
)