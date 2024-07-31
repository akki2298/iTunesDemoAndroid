package com.encora.topsongsapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.encora.topsongsapp.domain.model.Song
import com.encora.topsongsapp.domain.repository.MusicRepository
import com.encora.topsongsapp.util.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopSongsViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val application:Application
) : ViewModel() {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs
    val isNetworkAvailable: Boolean = isNetworkAvailable(application.applicationContext)

    init{
        getSongs()
    }

     fun getSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            val songsFromDb = repository.getSongsFromDb()
            if (songsFromDb.isEmpty()) {
                val songsFromApi = repository.getSongsFromApi()
                repository.saveSongsToDb(songsFromApi)
                _songs.value = songsFromApi
            } else {
                _songs.value = songsFromDb
            }
        }
    }
}