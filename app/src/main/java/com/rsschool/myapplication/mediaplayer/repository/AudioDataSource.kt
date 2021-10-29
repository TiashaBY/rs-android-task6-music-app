package com.rsschool.myapplication.mediaplayer.repository

import com.rsschool.myapplication.mediaplayer.model.AudioItem

interface AudioDataSource {
    fun getAllAudios(): List<AudioItem>
}
