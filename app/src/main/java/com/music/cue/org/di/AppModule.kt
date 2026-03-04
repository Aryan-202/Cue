package com.music.cue.org.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.music.cue.org.presentation.screens.player.CuePlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideCuePlayer(exoPlayer: ExoPlayer): CuePlayer {
        return CuePlayer(exoPlayer)
    }

}