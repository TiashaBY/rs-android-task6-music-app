package com.rsschool.myapplication.mediaplayer.repository

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.rsschool.myapplication.mediaplayer.R
import com.rsschool.myapplication.mediaplayer.model.AudioItem
import java.io.*

class AudioDataSourceImpl(private val context: Context) : AudioDataSource {

    override suspend fun getAllAudios(): List<AudioItem> {
        val jsonString =
            context.assets.open(context.getString(R.string.playlist_filename)).bufferedReader()
                .use { it.readText() }
        val mapper = ObjectMapper()
        val itemsList = mapper.readValue(jsonString, Array<AudioItem>::class.java)
        for (i in 0 until itemsList.size) {
            itemsList[i].apply { id = i.toString() }
        }
        return itemsList.toList()
    }
}