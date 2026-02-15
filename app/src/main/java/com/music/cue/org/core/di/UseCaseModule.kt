package com.music.cue.org.core.di

import com.music.cue.org.domain.repository.ISongRepository
import com.music.cue.org.domain.usecase.song.GetSongsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetSongsUseCase(
        songRepository: ISongRepository
    ): GetSongsUseCase {
        return GetSongsUseCase(songRepository)
    }
}