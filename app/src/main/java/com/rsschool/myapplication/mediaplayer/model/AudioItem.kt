package com.rsschool.myapplication.mediaplayer.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(value = ["id"], ignoreUnknown = true)
data class AudioItem (
        var id : String = "",
        val title : String = "",
        val artist : String = "",
        val bitmapUri : String = "",
        val trackUri : String = "",
        val duration : Long = 0
)
