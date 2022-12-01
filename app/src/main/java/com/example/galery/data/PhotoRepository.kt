package com.example.galery.data

import android.net.Uri
import com.example.galery.data.model.LoggedInUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(private val photoLocalDataSource: PhotoLocalDataSource, private val photoRemoteDataSource: PhotoRemoteDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    suspend fun logout() {
        photoRemoteDataSource.logout(user)
        user = null

    }

    private var cachedPhoto: List<Photo>? = null


    suspend fun login(user: User): Result<LoggedInUser> {

        val result = photoRemoteDataSource.login(user)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        user = loggedInUser
    }


    suspend fun registration(user: User): Result<Unit> {
        return photoRemoteDataSource.registration(user)
    }

    val allFavoritePhoto = photoLocalDataSource.getAllFavoritePhoto()

    suspend fun getFavoritePhoto(): List<PhotoEntity> {
        return  photoLocalDataSource.getAllPhoto()
    }

    suspend fun getPhoto(): MutableList<Photo> {
        val photo = photoLocalDataSource.getPhotoLocal()
        cachedPhoto = photo
        return photo
    }

    suspend fun sendPhotoToServer() {
        if (cachedPhoto == null) {
            if (getPhoto().isNotEmpty()) {
                photoRemoteDataSource.sendPhotoToServer(user!!.accessToken, cachedPhoto!!, user!!)
            }
        }
        else {
            photoRemoteDataSource.sendPhotoToServer(user!!.accessToken, cachedPhoto!!, user!!)
        }
    }
    suspend fun loadPhotoFromServer() {
        val namesPhoto = photoRemoteDataSource.fetchPhotoFromServer(user!!.accessToken, user!!)
        insertPhoto(namesPhoto, cachedPhoto)
    }
    suspend fun deletePhoto(uri: Uri) {
        photoLocalDataSource.deletePhoto(uri)
    }

    private suspend fun insertPhoto(namesPhoto: List<String>, cachedPhoto: List<Photo>?) {
        photoLocalDataSource.insertPhoto(namesPhoto, cachedPhoto)
    }

    suspend fun insertFavoritePhoto(key: String) {
        photoLocalDataSource.insertFavoritePhoto(PhotoEntity(key))
    }

    suspend fun checkPhoto(key: String): Boolean {
        return photoLocalDataSource.checkPhoto(key)
    }

    suspend fun deleteFavoritePhoto(key: String) {
        photoLocalDataSource.deleteFavoritePhoto(PhotoEntity(key))
    }

}