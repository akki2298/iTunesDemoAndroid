package com.encora.topsongsapp.di

import android.content.Context
import androidx.room.Room
import com.encora.topsongsapp.R
import com.encora.topsongsapp.data.local.SongDao
import com.encora.topsongsapp.data.local.SongDatabase
import com.encora.topsongsapp.data.remote.ApiService
import com.encora.topsongsapp.data.repository.MusicRepositoryImpl
import com.encora.topsongsapp.domain.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): SongDatabase {
        return Room.databaseBuilder(
            appContext,
            SongDatabase::class.java,
            appContext.getString(R.string.database_name)
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongDao(database: SongDatabase): SongDao {
        return database.songDao()
    }

    @Provides
    @Singleton
    fun provideSongRepository(
        apiService: ApiService,
        songDao: SongDao
    ): MusicRepository {
        return MusicRepositoryImpl(apiService,songDao)
    }
}