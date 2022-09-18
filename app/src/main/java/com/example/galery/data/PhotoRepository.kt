package com.example.galery.data

import android.net.Uri
import javax.inject.Inject

class PhotoRepository @Inject constructor(private val photoLocalDataSource: PhotoLocalDataSource) {
    suspend fun getPhoto(): MutableList<Photo> {
        return photoLocalDataSource.getPhotoLocal()
    }
    suspend fun deletePhoto(uri: Uri) {
        photoLocalDataSource.deletePhoto(uri)
    }
}