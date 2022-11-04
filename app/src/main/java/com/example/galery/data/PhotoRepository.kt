package com.example.galery.data

import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(private val photoLocalDataSource: PhotoLocalDataSource) {

    val allFavoritePhoto = photoLocalDataSource.getAllFavoritePhoto()

    suspend fun getFavoritePhoto(): List<PhotoEntity> {
        return  photoLocalDataSource.getAllPhoto()
    }

    suspend fun getPhoto(): MutableList<Photo> {
        return photoLocalDataSource.getPhotoLocal()
    }
    suspend fun deletePhoto(uri: Uri) {
        photoLocalDataSource.deletePhoto(uri)
    }

    suspend fun insertFavoritePhoto(key: String){
        photoLocalDataSource.insertFavoritePhoto(PhotoEntity(key))
    }

    suspend fun checkPhoto(key: String): Boolean {
        return photoLocalDataSource.checkPhoto(key)
    }

    suspend fun deleteFavoritePhoto(key: String) {
        photoLocalDataSource.deleteFavoritePhoto(PhotoEntity(key))
    }

}