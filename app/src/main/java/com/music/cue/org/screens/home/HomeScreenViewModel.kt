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
import androidx.core.net.toUri
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

enum class HomeTab {
    Songs, Albums, Artists, Folders
}

data class GroupedItem(
    val name: String,
    val songCount: Int,
    val artworkUri: String? = null
)

class HomeScreenViewModel(private val repository: SongRepos) : ViewModel() {
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    
    private val _displaySongs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _displaySongs.asStateFlow()

    private val _groupedItems = MutableStateFlow<List<GroupedItem>>(emptyList())
    val groupedItems: StateFlow<List<GroupedItem>> = _groupedItems.asStateFlow()

    private val _selectedGroup = MutableStateFlow<String?>(null)
    val selectedGroup: StateFlow<String?> = _selectedGroup.asStateFlow()

    private val _alphabetMap = MutableStateFlow<Map<Char, Int>>(emptyMap())
    val alphabetMap: StateFlow<Map<Char, Int>> = _alphabetMap.asStateFlow()

    private val _selectedTab = MutableStateFlow(HomeTab.Songs)
    val selectedTab: StateFlow<HomeTab> = _selectedTab.asStateFlow()

    private val _selectedSong = MutableStateFlow<Song?>(null)
    val selectedSong: StateFlow<Song?> = _selectedSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private var mediaController: MediaController? = null
    private var progressJob: Job? = null

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
                    if (isPlaying) {
                        startProgressUpdate()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val currentSong = _allSongs.value.find { it.contentUri == mediaItem?.mediaId }
                    if (currentSong != null) {
                        _selectedSong.value = currentSong
                    }
                    _duration.value = mediaController?.duration?.coerceAtLeast(0L) ?: 0L
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = mediaController?.duration?.coerceAtLeast(0L) ?: 0L
                    }
                }
            })
        }
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_isPlaying.value) {
                _currentPosition.value = mediaController?.currentPosition?.coerceAtLeast(0L) ?: 0L
                delay(500)
            }
        }
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
        _currentPosition.value = position
    }

    fun refreshSongs() {
        viewModelScope.launch {
            val allSongs = repository.fetchSongs()
            _allSongs.value = allSongs
            updateDisplay()
        }
    }

    fun onTabSelected(tab: HomeTab) {
        _selectedTab.value = tab
        _selectedGroup.value = null
        updateDisplay()
    }

    fun onGroupSelected(groupName: String) {
        _selectedGroup.value = groupName
        updateDisplay()
    }

    fun navigateBack() {
        if (_selectedGroup.value != null) {
            _selectedGroup.value = null
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        val tab = _selectedTab.value
        val group = _selectedGroup.value
        val all = _allSongs.value

        if (tab == HomeTab.Songs || group != null) {
            val filtered = when {
                group != null && tab == HomeTab.Artists -> all.filter { it.artist == group }
                group != null && tab == HomeTab.Albums -> all.filter { it.album == group }
                group != null && tab == HomeTab.Folders -> all.filter { it.folderName == group }
                else -> all
            }.sortedBy { it.title.lowercase() }
            
            _displaySongs.value = filtered
            _groupedItems.value = emptyList()
            updateAlphabetMap(filtered)
        } else {
            val grouped = when (tab) {
                HomeTab.Artists -> all.groupBy { it.artist }.map { 
                    GroupedItem(it.key, it.value.size, it.value.firstOrNull()?.albumArtUri) 
                }
                HomeTab.Albums -> all.groupBy { it.album }.map { 
                    GroupedItem(it.key, it.value.size, it.value.firstOrNull()?.albumArtUri) 
                }
                HomeTab.Folders -> all.groupBy { it.folderName }.map { 
                    GroupedItem(it.key, it.value.size, it.value.firstOrNull()?.albumArtUri) 
                }
                else -> emptyList()
            }.sortedBy { it.name.lowercase() }

            _groupedItems.value = grouped
            _displaySongs.value = emptyList()
            updateAlphabetMapForGroups(grouped)
        }
    }

    private fun updateAlphabetMap(songs: List<Song>) {
        _alphabetMap.value = songs.mapIndexed { index, song ->
            val char = song.title.firstOrNull()?.uppercaseChar() ?: '#'
            char to index
        }.distinctBy { it.first }.toMap()
    }

    private fun updateAlphabetMapForGroups(groups: List<GroupedItem>) {
        _alphabetMap.value = groups.mapIndexed { index, group ->
            val char = group.name.firstOrNull()?.uppercaseChar() ?: '#'
            char to index
        }.distinctBy { it.first }.toMap()
    }

    fun onSongSelected(song: Song) {
        _selectedSong.value = song
        mediaController?.let { controller ->
            val mediaItem = MediaItem.Builder()
                .setMediaId(song.contentUri)
                .setUri(song.contentUri.toUri())
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
