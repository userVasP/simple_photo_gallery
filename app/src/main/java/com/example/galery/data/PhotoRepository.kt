package com.example.galery.data

import android.net.Uri
import com.example.galery.data.model.LoggedInUser
import com.example.galery.data.model.Photo
import com.example.galery.data.model.Result
import com.example.galery.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val cachedPhotoMutex = Mutex()
    private var cachedPhoto: List<Photo> = emptyList()


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

    fun getFavoritePhoto(): List<PhotoEntity> {
        return  photoLocalDataSource.getAllPhoto()
    }

    fun getFavoritePhotoDataStream(): Flow<List<Photo>> {
        return photoLocalDataSource.getAllFavoritePhoto().map {
            getPhoto().filter {
                photo -> it.find { it.key == photo.getKey() } != null
            }
        }
    }

    suspend fun getPhoto(refresh: Boolean = false): List<Photo> {

        if (refresh || cachedPhoto.isEmpty()) {
            val photo = photoLocalDataSource.getPhotoLocal()

            cachedPhotoMutex.withLock {
                cachedPhoto = photo
            }
        }

        return cachedPhotoMutex.withLock { cachedPhoto }
    }

    suspend fun sendPhotoToServer() {
        photoRemoteDataSource.sendPhotoToServer(user!!.accessToken, cachedPhoto, user!!)
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