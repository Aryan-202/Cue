package com.music.cue.org.di

import com.music.cue.org.data.datasource.MediaStoreDataSource
import com.music.cue.org.data.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicModule {
    // MusicRepository and MediaStoreDataSource use @Inject constructors
    // so Hilt handles them automatically — no manual provides needed here
}