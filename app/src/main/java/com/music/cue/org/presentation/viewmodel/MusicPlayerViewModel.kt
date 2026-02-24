package com.music.cue.org.presentation.viewmodel

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.music.cue.org.data.model.Album
import com.music.cue.org.data.model.Artist
import com.music.cue.org.data.model.Song
import com.music.cue.org.model.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var exoPlayer: ExoPlayer? = null

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    private val _favoriteSongs = MutableStateFlow<List<Song>>(emptyList())
    val favoriteSongs: StateFlow<List<Song>> = _favoriteSongs

    private val _recentlyPlayed = MutableStateFlow<List<Song>>(emptyList())
    val recentlyPlayed: StateFlow<List<Song>> = _recentlyPlayed

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode

    init {
        loadSongsFromDevice()
        setupPlayer()
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    // Handle playback state changes
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    mediaItem?.let {
                        val song = songs.value.find { song ->
                            song.id.toString() == it.mediaId
                        }
                        _currentSong.value = song
                        song?.let { addToRecentlyPlayed(it) }
                    }
                }
            })
        }
    }

    private fun loadSongsFromDevice() {
        viewModelScope.launch {
            val songList = mutableListOf<Song>()
            val albumMap = mutableMapOf<Long, Album>()
            val artistMap = mutableMapOf<String, Artist>()

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val albumNameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn)
                    val artist = it.getString(artistColumn) ?: "Unknown Artist"
                    val duration = it.getLong(durationColumn)
                    val albumId = it.getLong(albumIdColumn)
                    val albumName = it.getString(albumNameColumn) ?: "Unknown Album"

                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    )

                    val song = Song(
                        id = id,
                        title = title,
                        artist = artist,
                        duration = duration,
                        albumArt = null, // You'd load this asynchronously
                        albumId = albumId,
                        isFavorite = false,
                    )
                    songList.add(song)

                    // Update albums
                    if (!albumMap.containsKey(albumId)) {
                        albumMap[albumId] = Album(
                            id = albumId,
                            name = albumName,
                            artist = artist,
                            songCount = 1,
                            albumArt = null
                        )
                    } else {
                        albumMap[albumId]?.let {
                            albumMap[albumId] = it.copy(songCount = it.songCount + 1)
                        }
                    }

                    // Update artists
                    if (!artistMap.containsKey(artist)) {
                        artistMap[artist] = Artist(
                            id = artistMap.size.toLong(),
                            name = artist,
                            songCount = 1,
                            albumCount = 1,
                            artistArt = null
                        )
                    } else {
                        artistMap[artist]?.let {
                            artistMap[artist] = it.copy(songCount = it.songCount + 1)
                        }
                    }
                }
            }

            _songs.value = songList
            _albums.value = albumMap.values.toList()
            _artists.value = artistMap.values.toList()

            // Create default playlists
            createDefaultPlaylists()
            setupMediaItems(songList)
        }
    }

    private fun createDefaultPlaylists() {
        val defaultPlaylists = listOf(
            Playlist(1, "Favorites", 0, null),
            Playlist(2, "Recently Added", 0, null),
            Playlist(3, "Most Played", 0, null),
            Playlist(4, "Top Rated", 0, null)
        )
        _playlists.value = defaultPlaylists
    }

    private fun setupMediaItems(songs: List<Song>) {
        val mediaItems = songs.map { song ->
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                song.id
            )
            MediaItem.Builder()
                .setUri(uri)
                .setMediaId(song.id.toString())
                .build()
        }
        exoPlayer?.setMediaItems(mediaItems)
        exoPlayer?.prepare()
    }

    private fun addToRecentlyPlayed(song: Song) {
        val currentList = _recentlyPlayed.value.toMutableList()
        currentList.removeAll { it.id == song.id }
        currentList.add(0, song)
        if (currentList.size > 20) {
            currentList.removeAt(currentList.size - 1)
        }
        _recentlyPlayed.value = currentList
    }

    fun toggleFavorite(song: Song) {
        val updatedSong = song.copy(isFavorite = !song.isFavorite)
        val songList = _songs.value.toMutableList()
        val index = songList.indexOfFirst { it.id == song.id }
        if (index != -1) {
            songList[index] = updatedSong
            _songs.value = songList

            if (updatedSong.isFavorite) {
                val favList = _favoriteSongs.value.toMutableList()
                favList.add(updatedSong)
                _favoriteSongs.value = favList
            } else {
                _favoriteSongs.value = _favoriteSongs.value.filter { it.id != song.id }
            }
        }
    }

    fun playPause() {
        if (_isPlaying.value) {
            exoPlayer?.pause()
        } else {
            exoPlayer?.play()
        }
    }

    fun playSong(song: Song) {
        val index = songs.value.indexOfFirst { it.id == song.id }
        if (index != -1) {
            exoPlayer?.seekTo(index, 0)
            exoPlayer?.play()
        }
    }

    fun playSongs(songList: List<Song>, startIndex: Int = 0) {
        val mediaItems = songList.map { song ->
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                song.id
            )
            MediaItem.Builder()
                .setUri(uri)
                .setMediaId(song.id.toString())
                .build()
        }
        exoPlayer?.setMediaItems(mediaItems, startIndex, 0)
        exoPlayer?.prepare()
        exoPlayer?.play()
    }

    fun skipToNext() {
        exoPlayer?.seekToNext()
    }

    fun skipToPrevious() {
        exoPlayer?.seekToPrevious()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
        exoPlayer?.shuffleModeEnabled = _isShuffleEnabled.value
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayer?.repeatMode = _repeatMode.value
    }

    fun getSongsByAlbum(albumId: Long): List<Song> {
        return songs.value.filter {
            val uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                it.id
            )
            uri.toString().contains("album_id=$albumId")
        }
    }

    fun getSongsByArtist(artistName: String): List<Song> {
        return songs.value.filter { it.artist == artistName }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
    }
}