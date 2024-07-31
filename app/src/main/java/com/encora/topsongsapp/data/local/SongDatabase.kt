package com.encora.topsongsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.encora.topsongsapp.data.model.SongEntity

@Database(entities = [SongEntity::class], version = 1)
abstract class SongDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}