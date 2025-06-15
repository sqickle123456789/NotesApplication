package com.sqickle.spacenotes.di

import android.content.Context
import com.sqickle.spacenotes.data.source.local.LocalNoteDataSource
import com.sqickle.spacenotes.data.source.local.room.AppDatabase
import com.sqickle.spacenotes.data.source.local.room.NoteDao
import com.sqickle.spacenotes.data.source.local.room.RoomNoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideLocalNoteDataSource(dao: NoteDao): LocalNoteDataSource {
        return RoomNoteDataSource(dao)
    }
}