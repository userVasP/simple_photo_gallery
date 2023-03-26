package com.example.galery.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photo: PhotoEntity): Completable
    @Query("SELECT * FROM favorite_photo_table WHERE `key` = :key")
    suspend fun searchPhoto(key:String): List<PhotoEntity>
    @Query("SELECT * FROM favorite_photo_table")
    fun getAllFavoritePhotoAsFlow(): Flowable<List<PhotoEntity>>
    @Query("SELECT * FROM favorite_photo_table")
    fun getAllFavoritePhoto(): Single<List<PhotoEntity>>
    @Delete
    fun delete(photoEntity: PhotoEntity): Completable
}