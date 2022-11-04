package com.example.galery.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PhotoEntity)
    @Query("SELECT * FROM favorite_photo_table WHERE `key` = :key")
    suspend fun searchPhoto(key:String): List<PhotoEntity>
    @Query("SELECT * FROM favorite_photo_table")
    fun getAllFavoritePhotoAsFlow(): Flow<List<PhotoEntity>>
    @Query("SELECT * FROM favorite_photo_table")
    fun getAllFavoritePhoto(): List<PhotoEntity>
    @Delete
    suspend fun delete(photoEntity: PhotoEntity)
}