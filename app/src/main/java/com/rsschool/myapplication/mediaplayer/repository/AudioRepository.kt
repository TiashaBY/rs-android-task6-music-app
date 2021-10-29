package com.rsschool.myapplication.mediaplayer.repository

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.rsschool.myapplication.mediaplayer.model.AudioItem
import javax.inject.Inject

class AudioRepository @Inject constructor(private val dataSource : AudioDataSource) {

    val audioMetadata: List<MediaMetadataCompat> by lazy  {getMediaMetaData()}
    private val audioItems: List<AudioItem> by lazy {dataSource.getAllAudios()}

    fun getMediaItems() : List<MediaItem> {
        return audioMetadata.map { song ->
            MediaItem.Builder()
                .setUri(song.description.mediaUri)
                .setMimeType(MimeTypes.BASE_TYPE_AUDIO)
                .build()
        }
    }

    private fun getMediaMetaData(): List<MediaMetadataCompat> {
        return audioItems.map { audio ->
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, audio.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, audio.id)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, audio.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, audio.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, audio.bitmapUri)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, audio.trackUri)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, audio.title)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audio.duration)
                .build()
        }
    }

    fun getMediaMetadataItems() = audioMetadata.map { audio ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(audio.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(audio.description.title)
            .setSubtitle(audio.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
            .setMediaId(audio.description.mediaId.toString())
            .setIconUri(audio.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()
}
