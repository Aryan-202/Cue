package com.music.cue.org.core.di

import android.content.Context
import com.music.cue.org.domain.repository.ISongRepository
import com.music.cue.org.domain.repository.SongRepository
import dagger.Provides

@Provides
fun provideSongRepository(
    context: Context,
): ISongRepository {
    return SongRepository(context)
}