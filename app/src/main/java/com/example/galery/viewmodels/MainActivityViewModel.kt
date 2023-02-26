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
class MainActivityViewModel @Inject constructor(private val photoRepository: PhotoRepository, @ApplicationContext context: Context) : ViewModel() {

    private val contentResolver = context.contentResolver
    private var contentObserver: ContentObserver? = null
    private var pendingDeleteImageUri: Uri? = null
    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()
    val permissionNeededForDelete: LiveData<IntentSender?> = _permissionNeededForDelete

    init {
        getPhoto()
    }

    fun getPhoto() {
        viewModelScope.launch {
            if (contentObserver == null) {
                contentObserver = object : ContentObserver(Handler()) {
                    override fun onChange(selfChange: Boolean) {
                        viewModelScope.launch {
                            checkSavedPhoto(this, true)
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

    fun checkSavedPhoto(coroutineScope: CoroutineScope, refresh: Boolean = false) {
        coroutineScope.launch {
            val allPhotoDef = async(Dispatchers.IO) { photoRepository.getPhoto(refresh) }
            val allFavoritePhotoDef = async(Dispatchers.IO) { photoRepository.getFavoritePhoto() }
            val allPhoto = allPhotoDef.await()
            val allFavoritePhoto = allFavoritePhotoDef.await()
            for (favoritePhoto in allFavoritePhoto) {
                if (allPhoto.find { it.getKey() == favoritePhoto.key } == null) {
                    photoRepository.deleteFavoritePhoto(favoritePhoto.key)
                }
            }

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