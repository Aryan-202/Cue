package com.music.cue.org.presentation.screens.songlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.cue.org.domain.usecase.song.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SongListState())
    val state = _state.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            getSongsUseCase().collect { songs ->
                _state.update { it.copy(songs = songs) }
            }
        }
    }
}
