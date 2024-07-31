package com.encora.topsongsapp.data.repository

import com.encora.topsongsapp.data.local.SongDao
import com.encora.topsongsapp.data.model.FeedEntry
import com.encora.topsongsapp.data.model.SongEntity
import com.encora.topsongsapp.data.remote.ApiService
import com.encora.topsongsapp.domain.model.Song
import com.encora.topsongsapp.domain.repository.MusicRepository
import com.encora.topsongsapp.util.formatDuration
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val songDao: SongDao
) : MusicRepository {


    override suspend fun getSongsFromApi(): List<Song> {
        return apiService.getSongsFromApi().entries.map { it.toDomain() }
    }

    override suspend fun getSongsFromDb(): List<Song> {
        return songDao.getSongs().map { it.toDomain() }
    }

    override suspend fun saveSongsToDb(songs: List<Song>) {
        songDao.insertSongs(songs.map { it.toEntity() })
    }

    private fun FeedEntry.toDomain(): Song {
        val shortId = id?.substringAfterLast("i=")?.substringBefore("&") ?: ""
        val image = images?.maxByOrNull { it.height ?: 0 }
        val link = links?.firstOrNull { it.type == "audio/x-m4a" }

        return Song(
            id = shortId,
            title = title ?: "",
            album = collection?.name ?: "",
            artist = artist?.name ?: "",
            songLink = link?.url ?: "",
            duration = formatDuration(duration ?: 0),
            imageUrl = image?.url ?: "",
            price = "${price?.amount} ${price?.currency}",
            releaseDate = releaseDate,
            genre = category?.label,
            copyRight = rights ?: ""
        )
    }

    private fun Song.toEntity(): SongEntity {
        return SongEntity(
            id = id,
            title = title,
            album = album,
            artist = artist,
            songLink = songLink,
            duration = duration,
            imageUrl = imageUrl,
            price = price,
            releaseDate = releaseDate ?: "",
            genre = genre ?: "",
            copyRight = copyRight,
        )
    }

    private fun SongEntity.toDomain(): Song {
        return Song(
            id = id,
            title = title,
            album = album,
            artist = artist,
            songLink = songLink,
            duration = duration,
            imageUrl = imageUrl,
            price = price,
            releaseDate = releaseDate,
            genre = genre,
            copyRight = copyRight
        )
    }
}
