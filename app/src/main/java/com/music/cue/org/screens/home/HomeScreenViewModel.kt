package com.music.cue.org.screens.home

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Immutable
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
import com.music.cue.org.utils.UserPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class HomeTab {
    Songs, Albums, Artists, Folders
}

@Immutable
data class GroupedItem(
    val name: String,
    val songCount: Int,
    val artworkUri: String? = null
)

class HomeScreenViewModel(
    private val repository: SongRepos,
    private val userPrefs: UserPrefs
) : ViewModel() {
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    
    private val _songsTab = MutableStateFlow<List<Song>>(emptyList())
    val songsTab: StateFlow<List<Song>> = _songsTab.asStateFlow()

    private val _artistsTab = MutableStateFlow<List<GroupedItem>>(emptyList())
    val artistsTab: StateFlow<List<GroupedItem>> = _artistsTab.asStateFlow()

    private val _albumsTab = MutableStateFlow<List<GroupedItem>>(emptyList())
    val albumsTab: StateFlow<List<GroupedItem>> = _albumsTab.asStateFlow()

    private val _foldersTab = MutableStateFlow<List<GroupedItem>>(emptyList())
    val foldersTab: StateFlow<List<GroupedItem>> = _foldersTab.asStateFlow()

    private val _displaySongs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _displaySongs.asStateFlow()

    private val _playbackQueue = MutableStateFlow<List<Song>>(emptyList())
    val playbackQueue: StateFlow<List<Song>> = _playbackQueue.asStateFlow()

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
                        userPrefs.saveLastSongId(currentSong.id)
                    }
                    _duration.value = mediaController?.duration?.coerceAtLeast(0L) ?: 0L
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = mediaController?.duration?.coerceAtLeast(0L) ?: 0L
                    }
                }
            })

            _selectedSong.value?.let { song ->
                val mediaItem = MediaItem.Builder()
                    .setMediaId(song.contentUri)
                    .setUri(song.contentUri.toUri())
                    .build()
                mediaController?.setMediaItem(mediaItem)
                mediaController?.seekTo(userPrefs.getLastPosition())
                mediaController?.prepare()
            }
        }
    }

    fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_isPlaying.value) {
                val position = mediaController?.currentPosition?.coerceAtLeast(0L) ?: 0L
                _currentPosition.value = position
                userPrefs.saveLastPosition(position)
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
            val allSongs = withContext(Dispatchers.IO) {
                repository.fetchSongs()
            }
            _allSongs.value = allSongs

            withContext(Dispatchers.Default) {
                val songsSorted = allSongs.sortedBy { it.title.lowercase() }
                val artists = allSongs.groupBy { it.artist }.map { 
                    GroupedItem(it.key, it.value.size, it.value.firstOrNull()?.albumArtUri) 
                }.sortedBy { it.name.lowercase() }
                val albums = allSongs.groupBy { it.album }.map { 
                    GroupedItem(it.key, it.value.size, it.value.firstOrNull()?.albumArtUri) 
                }.sortedBy { it.name.lowercase() }
                val folders = allSongs.groupBy { it.folderName }.map { 
                    GroupedItem(it.key, it.value.size, it.value.firstOrNull()?.albumArtUri) 
                }.sortedBy { it.name.lowercase() }

                _songsTab.value = songsSorted
                _artistsTab.value = artists
                _albumsTab.value = albums
                _foldersTab.value = folders
            }

            val lastId = userPrefs.getLastSongId()
            if (lastId != -1L && _selectedSong.value == null) {
                val lastSong = allSongs.find { it.id == lastId }
                if (lastSong != null) {
                    _selectedSong.value = lastSong
                    _playbackQueue.value = _songsTab.value
                    _duration.value = lastSong.durationMs
                    val lastPos = userPrefs.getLastPosition()
                    _currentPosition.value = lastPos

                    val mediaItems = _songsTab.value.map { s ->
                        MediaItem.Builder()
                            .setMediaId(s.contentUri)
                            .setUri(s.contentUri.toUri())
                            .build()
                    }
                    val startIndex = _songsTab.value.indexOfFirst { it.id == lastSong.id }.coerceAtLeast(0)

                    mediaController?.let { controller ->
                        controller.setMediaItems(mediaItems, startIndex, lastPos)
                        controller.prepare()
                    }
                }
            }
            
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

        viewModelScope.launch {
            if (group != null) {
                val filtered = withContext(Dispatchers.Default) {
                    when (tab) {
                        HomeTab.Artists -> all.filter { it.artist == group }
                        HomeTab.Albums -> all.filter { it.album == group }
                        HomeTab.Folders -> all.filter { it.folderName == group }
                        else -> all
                    }.sortedBy { it.title.lowercase() }
                }
                
                _displaySongs.value = filtered
                updateAlphabetMap(filtered)
            } else {
                when (tab) {
                    HomeTab.Songs -> updateAlphabetMap(_songsTab.value)
                    HomeTab.Artists -> updateAlphabetMapForGroups(_artistsTab.value)
                    HomeTab.Albums -> updateAlphabetMapForGroups(_albumsTab.value)
                    HomeTab.Folders -> updateAlphabetMapForGroups(_foldersTab.value)
                }
                
                if (tab == HomeTab.Songs) {
                    _displaySongs.value = _songsTab.value
                } else {
                    _displaySongs.value = emptyList()
                }
            }
        }
    }

    private fun updateAlphabetMap(songs: List<Song>) {
        viewModelScope.launch {
            val map = withContext(Dispatchers.Default) {
                songs.mapIndexed { index, song ->
                    val char = song.title.firstOrNull()?.uppercaseChar() ?: '#'
                    char to index
                }.distinctBy { it.first }.toMap()
            }
            _alphabetMap.value = map
        }
    }

    private fun updateAlphabetMapForGroups(groups: List<GroupedItem>) {
        viewModelScope.launch {
            val map = withContext(Dispatchers.Default) {
                groups.mapIndexed { index, group ->
                    val char = group.name.firstOrNull()?.uppercaseChar() ?: '#'
                    char to index
                }.distinctBy { it.first }.toMap()
            }
            _alphabetMap.value = map
        }
    }

    fun onSongSelected(song: Song) {
        _selectedSong.value = song
        userPrefs.saveLastSongId(song.id)
        
        val currentQueue = _displaySongs.value
        _playbackQueue.value = currentQueue

        mediaController?.let { controller ->
            val index = currentQueue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
            
            // Check if the current song is already the one selected to avoid redundant loading
            val currentMediaId = controller.currentMediaItem?.mediaId
            if (currentMediaId == song.contentUri) {
                if (!controller.isPlaying) controller.play()
                return
            }

            // Check if the song is already in the current playlist
            var foundIndex = -1
            for (i in 0 until controller.mediaItemCount) {
                if (controller.getMediaItemAt(i).mediaId == song.contentUri) {
                    foundIndex = i
                    break
                }
            }

            if (foundIndex != -1) {
                controller.seekTo(foundIndex, 0L)
                controller.play()
            } else {
                val mediaItems = currentQueue.map { s ->
                    MediaItem.Builder()
                        .setMediaId(s.contentUri)
                        .setUri(s.contentUri.toUri())
                        .build()
                }
                controller.setMediaItems(mediaItems, index, 0L)
                controller.prepare()
                controller.play()
            }
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

class HomeScreenViewModelFactory(
    private val repository: SongRepos,
    private val userPrefs: UserPrefs
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeScreenViewModel(repository, userPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
