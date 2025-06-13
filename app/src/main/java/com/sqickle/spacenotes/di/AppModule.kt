package com.sqickle.spacenotes.di

import android.content.Context
import com.sqickle.spacenotes.data.repository.NotesRepository
import com.sqickle.spacenotes.data.source.FileNoteDataSource
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
    fun provideNotesRepository(@ApplicationContext context: Context): NotesRepository {
        return FileNoteDataSource(context)
    }
}