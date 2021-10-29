package com.rsschool.myapplication.mediaplayer.repository

import android.content.Context
import com.rsschool.myapplication.mediaplayer.model.AudioItem
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.*

class AudioDataSourceImpl(private val context: Context) : AudioDataSource {

    override fun getAllAudios(): List<AudioItem> {
        val jsonString = context.assets.open("playlist.json").bufferedReader().use { it.readText() }
        val mapper = ObjectMapper()
        val itemsList = mapper.readValue(jsonString, Array<AudioItem>::class.java)
        for (i in 0 until itemsList.size) {
            itemsList[i].apply { id = i.toString() }
        }
        return itemsList.toList()
    }
}