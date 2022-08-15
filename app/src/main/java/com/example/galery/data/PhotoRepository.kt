package com.example.galery.data

import javax.inject.Inject

class PhotoRepository @Inject constructor(private val photoLocalDataSource: PhotoLocalDataSource) {
    suspend fun getPhoto(): MutableList<Photo> {
        return photoLocalDataSource.getPhotoLocal()
    }
}