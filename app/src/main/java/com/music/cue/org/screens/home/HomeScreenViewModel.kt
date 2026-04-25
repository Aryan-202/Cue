package com.music.cue.org.screens.home

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.music.cue.org.data.Song
import com.music.cue.org.playback.PlaybackService
import com.music.cue.org.repos.SongRepos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val repository: SongRepos) : ViewModel() {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _selectedSong = MutableStateFlow<Song?>(null)
    val selectedSong: StateFlow<Song?> = _selectedSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var mediaController: MediaController? = null

    init {
        refreshSongs()
    }

    fun initController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        viewModelScope.launch {
            mediaController = MediaController.Builder(context, sessionToken).buildAsync().await()
            mediaController?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val currentSong = _songs.value.find { it.contentUri == mediaItem?.mediaId }
                    if (currentSong != null) {
                        _selectedSong.value = currentSong
                    }
                }
            })
        }
    }

    fun refreshSongs() {
        viewModelScope.launch {
            _songs.value = repository.fetchSongs()
        }
    }

    fun onSongSelected(song: Song) {
        _selectedSong.value = song
        mediaController?.let { controller ->
            val mediaItem = MediaItem.Builder()
                .setMediaId(song.contentUri)
                .setUri(android.net.Uri.parse(song.contentUri))
                .build()
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        }
    }

    fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun next() {
        mediaController?.seekToNext()
    }

    fun previous() {
        mediaController?.seekToPrevious()
    }

    override fun onCleared() {
        super.onCleared()
        mediaController?.release()
    }
}

class HomeScreenViewModelFactory(private val repository: SongRepos) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
