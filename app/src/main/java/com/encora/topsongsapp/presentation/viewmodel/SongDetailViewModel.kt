package com.encora.topsongsapp.presentation.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encora.topsongsapp.domain.model.Song
import com.encora.topsongsapp.domain.repository.MusicRepository
import com.encora.topsongsapp.util.isNetworkAvailable
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SongDetailViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val application: Application
) : ViewModel() {

    private val _song = MutableStateFlow<Song?>(null)
    val song: StateFlow<Song?> = _song.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    val isNetworkAvailable: Boolean = isNetworkAvailable(application.applicationContext)

    private var exoPlayer: ExoPlayer? = null


    fun getSongDetail(songId: String) {
        viewModelScope.launch {
            val song = repository.getSongsFromDb().first { it.id == songId }
            _song.value = song
            exoPlayer?.release()
            song?.let {
                exoPlayer = ExoPlayer.Builder(application.applicationContext).build().apply {
                    setMediaItem(MediaItem.fromUri(Uri.parse(it.songLink)))
                    prepare()
                }
            }
        }
    }


    fun togglePlayPause() {
        if (isNetworkAvailable) {
            exoPlayer?.let {
                if (_isPlaying.value) {
                    it.pause()
                } else {
                    it.play()
                }
                _isPlaying.value = !_isPlaying.value
            }
        }
    }
    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}