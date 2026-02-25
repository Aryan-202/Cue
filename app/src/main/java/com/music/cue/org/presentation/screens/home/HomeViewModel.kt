// app/src/main/java/com/music/cue/org/presentation/screens/home/HomeViewModel.kt
package com.music.cue.org.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.cue.org.data.model.Song
import com.music.cue.org.domain.usercase.GetRecentSongsUseCase
import com.music.cue.org.domain.usercase.GetSongsUseCase
// import com.music.cue.org.presentation.viewmodel.MusicPlayerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val getRecentSongsUseCase: GetRecentSongsUseCase,
    // private val musicPlayerViewModel: MusicPlayerViewModel
) : ViewModel() {

    private val _recentSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentSongs: StateFlow<List<Song>> = _recentSongs

    // val currentSong = musicPlayerViewModel.currentSong
    // val isPlaying = musicPlayerViewModel.isPlaying

    init {
        loadRecentSongs()
    }

    private fun loadRecentSongs() {
        viewModelScope.launch {
            combine(
                getSongsUseCase(),
                getRecentSongsUseCase()
            ) { allSongs, recentIds ->
                allSongs.filter { song -> recentIds.contains(song.id) }
            }.collect { songs ->
                _recentSongs.value = songs
            }
        }
    }

    fun playSong(song: Song) {
        // musicPlayerViewModel.playSong(song)
    }

    fun playPause() {
        // musicPlayerViewModel.playPause()
    }

    fun skipToNext() {
        // musicPlayerViewModel.skipToNext()
    }

    fun skipToPrevious() {
        // musicPlayerViewModel.skipToPrevious()
    }
}