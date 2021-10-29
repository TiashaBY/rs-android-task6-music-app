package com.rsschool.myapplication.mediaplayer.ext

import android.support.v4.media.MediaMetadataCompat
import com.rsschool.myapplication.mediaplayer.model.AudioItem

fun MediaMetadataCompat.toAudioItem(): AudioItem {
        return AudioItem(
            id = this.description.mediaId ?: "",
            title = this.description.title.toString(),
            artist = this.description.subtitle.toString(),
            trackUri = this.description.mediaUri.toString(),
            bitmapUri = this.description.iconUri.toString()
        )
    }
