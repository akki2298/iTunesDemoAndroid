package com.encora.topsongsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class SongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: String,
    val imageUrl: String,
    val songLink:String,
    val price: String? = null,
    val releaseDate: String? = null,
    val genre: String? = null,
    val copyRight: String? = null
)
