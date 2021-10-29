package com.rsschool.myapplication.mediaplayer.di

import android.app.Application
import android.content.Context
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.rsschool.myapplication.mediaplayer.repository.AudioDataSourceImpl
import com.rsschool.myapplication.mediaplayer.repository.AudioRepository
import com.rsschool.myapplication.mediaplayer.ui.MediaController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AudioAppModule {

    @Provides
    @Singleton
    fun provideRepo(context: Application) = AudioRepository(AudioDataSourceImpl(context))

    @Provides
    @Singleton
    fun provideExoPlayer(context: Application
    ): SimpleExoPlayer {
        val attr = AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
        return SimpleExoPlayer.Builder(context.baseContext).build().apply {
            setAudioAttributes(attr, true)
            setHandleAudioBecomingNoisy(true)
            playWhenReady = true
        }
    }

    @Singleton
    @Provides
    fun provideMediaController(
        @ApplicationContext context: Context
    ) = MediaController(context)
}