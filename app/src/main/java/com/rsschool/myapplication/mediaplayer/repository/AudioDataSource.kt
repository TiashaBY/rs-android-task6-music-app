package com.rsschool.myapplication.mediaplayer.repository

import com.rsschool.myapplication.mediaplayer.model.AudioItem

interface AudioDataSource {
    suspend fun getAllAudios(): List<AudioItem>
}
