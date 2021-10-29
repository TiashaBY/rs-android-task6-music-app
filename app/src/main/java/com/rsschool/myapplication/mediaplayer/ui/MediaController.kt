package com.rsschool.myapplication.mediaplayer.ui

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rsschool.myapplication.mediaplayer.service.AudioPlayerMediaService
import com.rsschool.myapplication.mediaplayer.service.Constants.NETWORK_ERROR

class MediaController(context: Context) {

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected = _isConnected

    private val _networkError = MutableLiveData<Boolean>()
    val networkError = _networkError

    private val _playbackStateCompat = MutableLiveData<PlaybackStateCompat>()
    val playbackStateCompat = _playbackStateCompat

    private val _curPlayingSong = MutableLiveData<MediaMetadataCompat>()
    val curPlayingSong = _curPlayingSong

    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            AudioPlayerMediaService::class.java
        ),
        mediaBrowserConnectionCallback,
        null
    ).apply { connect() }


    /**
     * Of course, this is a two way channel and we haven’t talked about how to get information to your Service —
     * getTransportControls() fills that void, giving you methods to trigger any action
     * (including custom actions specific to your media playback such as ‘skip forward 30 seconds’).
     * All of which directly trigger the methods in your MediaSessionCompat.Callback in your Service.
     */
    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            Log.d("MusicServiceConnection", "CONNECTED")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.postValue(true)
        }

        override fun onConnectionSuspended() {
            Log.d("MusicServiceConnection", "SUSPENDED")
            _isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            Log.d("MusicServiceConnection", "FAILED")
            _isConnected.postValue(false)
        }
    }

    /**
     * there’s a MediaControllerCompat.Callback you can pass to registerCallback().
     * You’ll get a callback whenever anything changes, allowing your UI to stay in sync with your Service.
     */
    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackStateCompat.postValue(state!!)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _curPlayingSong.postValue(metadata!!)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event) {
                NETWORK_ERROR -> _networkError.postValue(true)
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}
