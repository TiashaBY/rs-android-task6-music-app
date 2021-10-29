package com.rsschool.myapplication.mediaplayer.service

import android.widget.Toast
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.rsschool.myapplication.mediaplayer.service.AudioPlayerMediaService

class PlayerEventsListener(private val musicService: AudioPlayerMediaService) : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (!isPlaying){
                musicService.stopForeground(false)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Toast.makeText(musicService, "Music App: an unknown error occurred", Toast.LENGTH_LONG).show()
        }
}