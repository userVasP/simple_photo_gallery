package com.example.galery.viewmodels

import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.lifecycle.*
import com.example.galery.data.*
import com.example.galery.data.model.Photo
import com.example.galery.utilities.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val photoRepository: PhotoRepository, @ApplicationContext context: Context) : ObservableViewModel() {


    var isPermissionGranted = MutableLiveData(false)
    var isFirstLoadedPhoto = false
    private var _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photos
    private var _favoritePhotos = MutableLiveData<List<Photo>>()
    val favoritePhotos: LiveData<List<Photo>> = _favoritePhotos
    private val contentResolver = context.contentResolver
    private var contentObserver: ContentObserver? = null
    private var pendingDeleteImageUri: Uri? = null
    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()
    val permissionNeededForDelete: LiveData<IntentSender?> = _permissionNeededForDelete

    private var _isCurrentPhotoFavorite = MutableLiveData<Boolean>()
    val isCurrentPhotoFavorite: LiveData<Boolean> = _isCurrentPhotoFavorite

    fun addFavoritePhoto(key: String) {
        viewModelScope.launch {
            photoRepository.insertFavoritePhoto(key)
            _photos.value?.find { it.getKey() == key }?.let {
                if (_favoritePhotos.value?.contains(it) == false) {
                    (_favoritePhotos.value as MutableList).add(it)
                    _isCurrentPhotoFavorite.value = true
                }
            }
        }
    }

    fun removeFavoritePhoto(key: String) {
        viewModelScope.launch {
            photoRepository.deleteFavoritePhoto(key)
            _photos.value?.find { it.getKey() == key }
                ?.let {
                    (_favoritePhotos.value as MutableList).remove(it)
                    _isCurrentPhotoFavorite.value = false
                }
        }
    }

    fun checkPhoto(key: String) {
        _isCurrentPhotoFavorite.value = (_favoritePhotos.value?.find { it.getKey() == key }) != null
    }

    /*fun checkPhoto(uri: String) {

        viewModelScope.launch {
            _isCurrentPhotoFavorite.value = photoRepository.checkPhoto(uri)
        }
    }*/

    fun getPhoto() {
        viewModelScope.launch {
            if (contentObserver == null) {
                contentObserver = object : ContentObserver(Handler()) {
                    override fun onChange(selfChange: Boolean) {
                        viewModelScope.launch {
                            checkSavedPhoto(this)
                        }
                    }
                }
                contentResolver.registerContentObserver(
                    CollectionUri.getCollectionUri(), true,
                    contentObserver as ContentObserver
                )
            }
            checkSavedPhoto(this)
        }
    }

    fun checkSavedPhoto(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val allPhotoDef = async(Dispatchers.IO) { photoRepository.getPhoto() }
            val allFavoritePhotoDef = async(Dispatchers.IO) { photoRepository.getFavoritePhoto() }
            val allPhoto = allPhotoDef.await()
            val allFavoritePhoto = allFavoritePhotoDef.await()
            for (favoritePhoto in allFavoritePhoto) {
                if (allPhoto.find { it.getKey() == favoritePhoto.key } == null) {
                    photoRepository.deleteFavoritePhoto(favoritePhoto.key)
                }
            }
            _photos.postValue(photoRepository.getPhoto())
            _favoritePhotos.postValue(
                allPhoto.filter { photo ->
                    allFavoritePhoto.find {
                        it.key == photo.getKey()
                    } != null
                }
            )
        }

    }

    fun deletePendingPhoto() {
        pendingDeleteImageUri?.let { deletePhoto(it) }
    }

    fun deleteChosenPhoto(uri: Uri) {
        deletePhoto(uri)
    }

    private fun deletePhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                photoRepository.deletePhoto(uri)
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException =
                        securityException as? RecoverableSecurityException
                            ?: throw securityException

                    pendingDeleteImageUri = uri
                    _permissionNeededForDelete.postValue(
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    )
                } else {
                    throw securityException
                }
            }
        }
    }

    override fun onCleared() {
        contentObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
    }
}