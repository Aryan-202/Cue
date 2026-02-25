package com.music.cue.org.data.repository

import android.content.Context
import com.music.cue.org.data.datasource.MediaStoreDataSource
import com.music.cue.org.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

//private val Context.dataStore by preferencesDataStore("music_preferences")

@Singleton
class MusicRepository @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: Flow<List<Song>> = _songs.asStateFlow()

    private val _favoriteSongIds = MutableStateFlow<Set<Long>>(emptySet())
    val favoriteSongIds: Flow<Set<Long>> = _favoriteSongIds.asStateFlow()

    private val _recentlyPlayed = MutableStateFlow<List<Long>>(emptyList())
    val recentlyPlayed: Flow<List<Long>> = _recentlyPlayed.asStateFlow()

    private val _playlists = MutableStateFlow<Map<Long, List<Long>>>(emptyMap())
    val playlists: Flow<Map<Long, List<Long>>> = _playlists.asStateFlow()

    init {
        loadSongs()
        loadPreferences()
    }

    private fun loadSongs() {
        coroutineScope.launch {
            mediaStoreDataSource.getSongsFromDevice().collect { songs ->
                _songs.value = songs
            }
        }
    }

    private fun loadPreferences() {
        coroutineScope.launch {
            // In a real app, this would load from DataStore or Room
            // For now, we'll use mock data
            _favoriteSongIds.value = emptySet()
            _recentlyPlayed.value = emptyList()
        }
    }

    fun getAllSongs(): Flow<List<Song>> = songs

    fun getSongById(id: Long): Song? = _songs.value.find { it.id == id }

    fun getFavoriteSongs(): Flow<List<Song>> = songs.map { allSongs ->
        allSongs.filter { it.id in _favoriteSongIds.value }
    }

    fun toggleFavorite(songId: Long) {
        coroutineScope.launch {
            val current = _favoriteSongIds.value.toMutableSet()
            if (current.contains(songId)) {
                current.remove(songId)
            } else {
                current.add(songId)
            }
            _favoriteSongIds.value = current
            saveFavorites(current)
        }
    }

    fun addToRecentlyPlayed(songId: Long) {
        coroutineScope.launch {
            val current = _recentlyPlayed.value.toMutableList()
            current.remove(songId)
            current.add(0, songId)
            if (current.size > 50) current.removeAt(current.lastIndex)
            _recentlyPlayed.value = current
            saveRecentlyPlayed(current)
        }
    }

    fun getRecentlyPlayedIds(): Flow<List<Long>> = recentlyPlayed

    fun searchSongs(query: String): Flow<List<Song>> = songs.map { allSongs ->
        if (query.isBlank()) {
            emptyList()
        } else {
            allSongs.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artist.contains(query, ignoreCase = true) ||
                        it.album.contains(query, ignoreCase = true)
            }
        }
    }

    private suspend fun saveFavorites(favorites: Set<Long>) {
        // In a real app, save to DataStore
        // This is a placeholder
    }

    private suspend fun saveRecentlyPlayed(recent: List<Long>) {
        // In a real app, save to DataStore
        // This is a placeholder
    }
}