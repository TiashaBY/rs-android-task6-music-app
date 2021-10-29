package com.rsschool.myapplication.mediaplayer.service

import android.app.PendingIntent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.rsschool.myapplication.mediaplayer.model.AudioItem
import com.rsschool.myapplication.mediaplayer.notification.NotificationManager
import com.rsschool.myapplication.mediaplayer.repository.AudioRepository
import com.rsschool.myapplication.mediaplayer.service.Constants.MEDIA_ROOT_ID
import com.rsschool.myapplication.mediaplayer.service.Constants.NETWORK_ERROR
import com.rsschool.myapplication.mediaplayer.service.Constants.SERVICE_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerMediaService() : MediaBrowserServiceCompat() {

    @Inject
    lateinit var repo: AudioRepository

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    var isForegroundService = false
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var musicNotificationManager: NotificationManager

    private var currentSong: MediaMetadataCompat? = null
    private var isPlayerInitialized = false
    private lateinit var musicPlayerEventListener: PlayerEventsListener

    override fun onCreate() {
        super.onCreate()
        // Build a PendingIntent that can be used to launch the UI.
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(
                        PlaybackStateCompat.ACTION_PLAY
                                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    ).build()
            )
        }

        sessionToken = mediaSession.sessionToken
        musicNotificationManager = NotificationManager(this, sessionToken!!, this)

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(CustomPlaybackPreparer())
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)

        musicPlayerEventListener = PlayerEventsListener(this)
        exoPlayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return repo.audioMetadata[windowIndex].description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            MEDIA_ROOT_ID -> {
                if (repo.audioMetadata.isNotEmpty()) {
                    result.sendResult(repo.getMediaMetadataItems())
                    if (!isPlayerInitialized) {
                        preparePlayer(repo.audioMetadata, repo.audioMetadata[0], false)
                        isPlayerInitialized = true
                    }
                } else {
                    mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                    result.sendResult(null)
                }
            }
        }
    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat,
        playNow: Boolean
    ) {
        val songIndex = if (currentSong == null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.setMediaItems(repo.getMediaItems())
        exoPlayer.prepare()
        exoPlayer.seekTo(songIndex, 0L)
        exoPlayer.playWhenReady = playNow
    }

    inner class CustomPlaybackPreparer : MediaSessionConnector.PlaybackPreparer {
        override fun onCommand(
            player: Player,
            controlDispatcher: ControlDispatcher,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ) = false

        override fun getSupportedPrepareActions(): Long {
            return PlaybackStateCompat.ACTION_PREPARE_FROM_URI or
                    PlaybackStateCompat.ACTION_PLAY_FROM_URI
        }

        override fun onPrepare(playWhenReady: Boolean) = Unit

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) =
            Unit

        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) =
            Unit

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
            val itemToPlay = repo.audioMetadata.find { uri == it.description.mediaUri }
            if (itemToPlay != null) {
                currentSong = itemToPlay
                preparePlayer(
                    repo.audioMetadata,
                    itemToPlay,
                    true
                )
            }

        }
    }
}



