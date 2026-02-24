package com.music.cue.org.data.repository

import com.music.cue.org.data.datasource.MediaStoreDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MusicRepository (
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

}