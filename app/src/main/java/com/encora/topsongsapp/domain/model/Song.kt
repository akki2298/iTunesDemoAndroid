package com.encora.topsongsapp.domain.model

data class Song(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: String,
    val imageUrl: String,
    val songLink:String,
    val price: String? = null,
    val releaseDate: String? = null,
    val genre: String? = null,
    val copyRight: String? = null,
)
