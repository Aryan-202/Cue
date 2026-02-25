package com.music.cue.org.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.cue.org.data.model.Album
import com.music.cue.org.data.model.Artist
import com.music.cue.org.data.model.Song
import com.music.cue.org.data.repository.MusicRepository
import com.music.cue.org.data.repository.PlaybackRepository
import com.music.cue.org.data.repository.PlaybackRepository.RepeatMode
import com.music.cue.org.data.model.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playbackRepository: PlaybackRepository
) : ViewModel() {

    // Songs
    val songs: StateFlow<List<Song>> = musicRepository.getAllSongs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteSongs: StateFlow<List<Song>> = musicRepository.getFavoriteSongs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Playback state
    val currentSong: StateFlow<Song?> = playbackRepository.currentSong
    val isPlaying: StateFlow<Boolean> = playbackRepository.isPlaying
    val playbackState: StateFlow<PlaybackRepository.PlaybackState> = playbackRepository.playbackState
    val currentPosition: StateFlow<Long> = playbackRepository.currentPosition
    val duration: StateFlow<Long> = playbackRepository.duration
    val repeatMode: StateFlow<RepeatMode> = playbackRepository.repeatMode
    val shuffleMode: StateFlow<Boolean> = playbackRepository.shuffleMode

    // Recently played
    val recentlyPlayed: StateFlow<List<Song>> = musicRepository.getRecentlyPlayedIds()
        .combine(songs) { recentIds, allSongs ->
            recentIds.mapNotNull { id -> allSongs.find { it.id == id } }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Albums (mock data for now - in a real app, you'd get this from the repository)
    val albums = MutableStateFlow<List<Album>>(emptyList())

    // Artists (mock data for now)
    val artists = MutableStateFlow<List<Artist>>(emptyList())

    // Playlists (mock data for now)
    val playlists = MutableStateFlow<List<Playlist>>(emptyList())

    init {
        loadMockData()
    }

    private fun loadMockData() {
        viewModelScope.launch {
            // In a real app, you'd load from repository
            // This is just for UI testing
            albums.value = listOf(
                Album(1, "Album 1", "Artist 1", 10, null),
                Album(2, "Album 2", "Artist 2", 8, null),
                Album(3, "Album 3", "Artist 3", 12, null)
            )

            artists.value = listOf(
                Artist(1, "Artist 1", 10, 2, null),
                Artist(2, "Artist 2", 8, 1, null),
                Artist(3, "Artist 3", 12, 3, null)
            )

            playlists.value = listOf(
                Playlist(1, "Favorites", 0, null),
                Playlist(2, "Workout Mix", 0, null),
                Playlist(3, "Chill Vibes", 0, null)
            )
        }
    }

    fun loadSongs() {
        // Songs are automatically loaded in the repository
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            val allSongs = songs.value
            val index = allSongs.indexOfFirst { it.id == song.id }
            if (index >= 0) {
                playbackRepository.setPlaylist(allSongs, index)
                playbackRepository.play()
                musicRepository.addToRecentlyPlayed(song.id)
            }
        }
    }

    fun playSongs(songs: List<Song>, startIndex: Int) {
        viewModelScope.launch {
            playbackRepository.setPlaylist(songs, startIndex)
            playbackRepository.play()
            if (songs.isNotEmpty() && startIndex in songs.indices) {
                musicRepository.addToRecentlyPlayed(songs[startIndex].id)
            }
        }
    }

    fun playPause() {
        playbackRepository.playPause()
    }

    fun skipToNext() {
        playbackRepository.skipToNext()
        currentSong.value?.let { musicRepository.addToRecentlyPlayed(it.id) }
    }

    fun skipToPrevious() {
        playbackRepository.skipToPrevious()
        currentSong.value?.let { musicRepository.addToRecentlyPlayed(it.id) }
    }

    fun seekTo(position: Long) {
        playbackRepository.seekTo(position)
    }

    fun toggleFavorite(song: Song) {
        musicRepository.toggleFavorite(song.id)
    }

    fun setRepeatMode(mode: RepeatMode) {
        playbackRepository.setRepeatMode(mode)
    }

    fun setShuffleMode(enabled: Boolean) {
        playbackRepository.setShuffleMode(enabled)
    }

    fun searchSongs(query: String): Flow<List<Song>> = musicRepository.searchSongs(query)

    override fun onCleared() {
        super.onCleared()
        playbackRepository.release()
    }
}