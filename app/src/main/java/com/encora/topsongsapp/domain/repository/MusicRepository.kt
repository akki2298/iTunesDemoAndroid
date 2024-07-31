package com.encora.topsongsapp.domain.repository

import com.encora.topsongsapp.domain.model.Song

interface MusicRepository {
    suspend fun getSongsFromApi(): List<Song>
    suspend fun getSongsFromDb(): List<Song>
    suspend fun saveSongsToDb(songs: List<Song>)
}