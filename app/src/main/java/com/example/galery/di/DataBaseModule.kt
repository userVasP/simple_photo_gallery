package com.example.galery.di

import android.content.Context
import androidx.room.Room
import com.example.galery.data.AppDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataBaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDataBase{
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "favorite_photo_database"
        ).build() // The reason we can construct a database for the repo
    }

    @Singleton
    @Provides
    fun providePhotoDao(db: AppDataBase) = db.photoDao()
}