package com.example.galery.data

import android.net.Uri
import com.example.galery.data.model.LoggedInUser
import com.example.galery.data.model.Photo
import com.example.galery.data.model.Result
import com.example.galery.data.model.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(private val photoLocalDataSource: PhotoLocalDataSource, private val photoRemoteDataSource: PhotoRemoteDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null


    fun logout(): Completable {
        return photoRemoteDataSource.logout(user).doAfterTerminate {
            user = null
        }

    }

    private val cachedPhotoMutex = Any()
    private var cachedPhoto: List<Photo> = emptyList()

    fun login(user: User): Single<LoggedInUser> {
        return photoRemoteDataSource.login(user).doOnSuccess { result -> setLoggedInUser(result) }
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        user = loggedInUser
    }



    fun registration(user: User): Completable {
        return photoRemoteDataSource.registration(user)
    }


    fun getFavoritePhoto(): Single<List<PhotoEntity>> {
        return  photoLocalDataSource.getAllPhoto()
    }


    fun getFavoritePhotoDataStream(): Flowable<List<Photo>> {
        return photoLocalDataSource.getAllFavoritePhoto().flatMap {
            favoritePhoto -> getPhoto().map { photos ->
            photos.filter {
                    photo -> favoritePhoto.find { it.key == photo.getKey() } != null
            }
        }
            .toFlowable()
        }
    }

    fun getPhoto(refresh: Boolean = false): Single<List<Photo>> {

        if (refresh || cachedPhoto.isEmpty()) {
            return getPhotoAsSingle().doOnSuccess { synchronized(cachedPhotoMutex) {
                cachedPhoto = it
            } }.map {
                list ->
                list
            }
        }

        return synchronized(cachedPhotoMutex) { Single.just(cachedPhoto) }
    }

    fun getPhotoAsSingle(): Single<MutableList<Photo>> {
        return photoLocalDataSource.getPhotoLocal()
    }

    fun sendPhotoToServer(): Completable {
        return photoRemoteDataSource.sendPhotoToServer(user!!.accessToken, cachedPhoto, user!!)
    }

    fun loadPhotoFromServer(): Completable {
        val ph = synchronized(cachedPhotoMutex) { cachedPhoto }
        return photoRemoteDataSource.fetchPhotoFromServer(user!!.accessToken, user!!)
            .doAfterSuccess {
            insertPhoto(it, ph)
        }
            .ignoreElement()
    }

    suspend fun deletePhoto(uri: Uri) {
        photoLocalDataSource.deletePhoto(uri)
    }

    private fun insertPhoto(namesPhoto: List<String>, cachedPhoto: List<Photo>?) {
        photoLocalDataSource.insertPhoto(namesPhoto, cachedPhoto)
    }

    fun insertFavoritePhoto(key: String): Completable {
        return photoLocalDataSource.insertFavoritePhoto(PhotoEntity(key))
    }

    suspend fun checkPhoto(key: String): Boolean {
        return photoLocalDataSource.checkPhoto(key)
    }

    fun deleteFavoritePhoto(key: String): Completable {
        return photoLocalDataSource.deleteFavoritePhoto(PhotoEntity(key))
    }
}