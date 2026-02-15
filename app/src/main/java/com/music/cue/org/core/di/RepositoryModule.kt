package com.music.cue.org.core.di

import android.content.Context
import com.music.cue.org.data.repository.SongRepository
import com.music.cue.org.domain.repository.ISongRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSongRepository(
        @ApplicationContext context: Context
    ): ISongRepository {
        return SongRepository(context)
    }
}