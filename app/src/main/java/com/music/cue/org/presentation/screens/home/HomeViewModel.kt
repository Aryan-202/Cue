package com.music.cue.org.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.cue.org.data.model.Genre
import com.music.cue.org.data.model.MostPlayedSong
import com.music.cue.org.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _mostPlayedSongs = MutableStateFlow<HomeUiState<List<MostPlayedSong>>>(HomeUiState.Loading)
    val mostPlayedSongs: StateFlow<HomeUiState<List<MostPlayedSong>>> = _mostPlayedSongs

    private val _genres = MutableStateFlow<HomeUiState<List<Genre>>>(HomeUiState.Loading)
    val genres: StateFlow<HomeUiState<List<Genre>>> = _genres

    private val _recentlyPlayed = MutableStateFlow<HomeUiState<List<MostPlayedSong>>>(HomeUiState.Loading)
    val recentlyPlayed: StateFlow<HomeUiState<List<MostPlayedSong>>> = _recentlyPlayed

    private val _recommendedForYou = MutableStateFlow<HomeUiState<List<MostPlayedSong>>>(HomeUiState.Loading)
    val recommendedForYou: StateFlow<HomeUiState<List<MostPlayedSong>>> = _recommendedForYou

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            // Load most played songs
            homeRepository.getMostPlayedSongs()
                .catch { e -> _mostPlayedSongs.value = HomeUiState.Error(e.message ?: "Unknown error") }
                .collect { songs ->
                    // Simulate loading delay for better UX
                    delay(500)
                    _mostPlayedSongs.value = HomeUiState.Success(songs)
                }
        }

        viewModelScope.launch {
            // Load genres
            homeRepository.getGenres()
                .catch { e -> _genres.value = HomeUiState.Error(e.message ?: "Unknown error") }
                .collect { genreList ->
                    _genres.value = HomeUiState.Success(genreList)
                }
        }

        viewModelScope.launch {
            // Load recently played
            homeRepository.getRecentlyPlayed()
                .catch { e -> _recentlyPlayed.value = HomeUiState.Error(e.message ?: "Unknown error") }
                .collect { songs ->
                    _recentlyPlayed.value = HomeUiState.Success(songs)
                }
        }

        viewModelScope.launch {
            // Load recommended for you
            homeRepository.getRecommendedForYou()
                .catch { e -> _recommendedForYou.value = HomeUiState.Error(e.message ?: "Unknown error") }
                .collect { songs ->
                    _recommendedForYou.value = HomeUiState.Success(songs)
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSongClick(song: MostPlayedSong) {
        // Handle song click - navigate to player or play the song
    }

    fun onGenreClick(genre: Genre) {
        // Handle genre click - navigate to genre songs
    }

    fun onSeeAllClick(section: String) {
        // Handle see all click - navigate to full list
    }
}

sealed class HomeUiState<out T> {
    object Loading : HomeUiState<Nothing>()
    data class Success<T>(val data: T) : HomeUiState<T>()
    data class Error(val message: String) : HomeUiState<Nothing>()
}