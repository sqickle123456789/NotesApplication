package com.sqickle.spacenotes.di

import android.content.Context
import com.sqickle.spacenotes.data.repository.NotesRepository
import com.sqickle.spacenotes.data.repository.NotesRepositoryImpl
import com.sqickle.spacenotes.data.source.local.LocalNoteDataSource
import com.sqickle.spacenotes.data.source.remote.RemoteNoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideLocalDataSource(@ApplicationContext context: Context): LocalNoteDataSource {
        return LocalNoteDataSource(context)
    }

    @Provides
    @Singleton
    fun provideNotesRepository(
        localDataSource: LocalNoteDataSource,
        remoteDataSource: RemoteNoteDataSource
    ): NotesRepository {
        return NotesRepositoryImpl(localDataSource, remoteDataSource)
    }
}