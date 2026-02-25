package com.music.cue.org.di

import android.content.Context

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.music.cue.org.data.datasource.MediaStoreDataSource
import com.music.cue.org.data.repository.MusicRepository
import com.music.cue.org.data.repository.PlaybackRepository
import com.music.cue.org.domain.usercase.GetRecentSongsUseCase
import com.music.cue.org.domain.usercase.GetSongsUseCase
import com.music.cue.org.util.PermissionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideMediaStoreDataSource(
        @ApplicationContext context: Context
    ): MediaStoreDataSource = MediaStoreDataSource(context)

    @Provides
    @Singleton
    fun provideMusicRepository(
        mediaStoreDataSource: MediaStoreDataSource,
        coroutineScope: CoroutineScope
    ): MusicRepository = MusicRepository(mediaStoreDataSource, coroutineScope)

    @Provides
    @Singleton
    fun providePermissionManager(
        @ApplicationContext context: Context
    ): PermissionManager = PermissionManager(context)

    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer = ExoPlayer.Builder(context).build()

    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ): MediaSession = MediaSession.Builder(context, player).build()

    @Provides
    @Singleton
    fun providePlaybackRepository(
        @ApplicationContext context: Context,
        player: ExoPlayer,
        mediaSession: MediaSession
    ): PlaybackRepository = PlaybackRepository(context, player, mediaSession)

    @Provides
    @Singleton
    fun provideGetSongsUseCase(
        musicRepository: MusicRepository
    ): GetSongsUseCase = GetSongsUseCase(musicRepository)

    @Provides
    @Singleton
    fun provideGetRecentSongsUseCase(
        musicRepository: MusicRepository
    ): GetRecentSongsUseCase = GetRecentSongsUseCase(musicRepository)
}