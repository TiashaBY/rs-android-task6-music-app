package com.rsschool.myapplication.mediaplayer.ui

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rsschool.myapplication.mediaplayer.ext.Constants.MEDIA_ROOT_ID
import com.rsschool.myapplication.mediaplayer.ext.isPlayEnabled
import com.rsschool.myapplication.mediaplayer.ext.isPlaying
import com.rsschool.myapplication.mediaplayer.ext.isPrepared
import com.rsschool.myapplication.mediaplayer.model.AudioItem
import com.rsschool.myapplication.mediaplayer.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioItemViewModel @Inject constructor(
    val mediaController: MediaController,
    val repository: AudioRepository
) : ViewModel() {
    var audioList: MutableLiveData<List<AudioItem>> = MutableLiveData()

    val isConnected = mediaController.isConnected
    val networkError = mediaController.networkError
    val playbackStateCompat = mediaController.playbackStateCompat
    val curPlayingSong = mediaController.curPlayingSong

    init {
        mediaController.subscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val items = children.map {
                        AudioItem(
                            id = it.description.mediaId ?: "",
                            title = it.description.title.toString(),
                            artist = it.description.subtitle.toString(),
                            trackUri = it.description.mediaUri.toString(),
                            bitmapUri = it.description.iconUri.toString()
                        )
                    }
                    audioList.value = items
                }
            })
    }

    fun skipToNextSong() {
        mediaController.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        mediaController.transportControls.skipToPrevious()
    }

    fun fastForward() {
        mediaController.transportControls.fastForward()
    }

    fun rewind() {
        mediaController.transportControls.rewind()
    }

    fun seekTo(pos: Long) {
        mediaController.transportControls.seekTo(pos)
    }

    override fun onCleared() {
        super.onCleared()
        mediaController.unsubscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }

    fun playOrToggleSong(mediaItem: AudioItem, toggle: Boolean = false) {
        if (playbackStateCompat.value?.isPrepared == true &&
            mediaItem.id == curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
        ) {
            playbackStateCompat.value?.let {
                when {
                    it.isPlaying ->
                        if (toggle) mediaController.transportControls.pause()

                    it.isPlayEnabled ->
                        mediaController.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            mediaController.transportControls.playFromUri(Uri.parse(mediaItem.trackUri), null)
        }
    }
}
