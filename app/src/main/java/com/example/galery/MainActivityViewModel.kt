package com.example.galery

import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galery.data.CollectionUri
import com.example.galery.data.Photo
import com.example.galery.data.PhotoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val photoRepository: PhotoRepository, context: Context) : ViewModel() {
    var isPermissionGranted = MutableLiveData(false)
    var isFirstLoadedPhoto = false
    private var _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photos
    private val contentResolver = context.contentResolver
    private var contentObserver: ContentObserver? = null
    private var pendingDeleteImageUri: Uri? = null
    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()
    val permissionNeededForDelete: LiveData<IntentSender?> = _permissionNeededForDelete

    fun getPhoto() {
        viewModelScope.launch {
            if (contentObserver == null) {
                contentObserver = object : ContentObserver(Handler()) {
                    override fun onChange(selfChange: Boolean) {
                        getPhoto()
                    }
                }
                contentResolver.registerContentObserver(
                    CollectionUri.getCollectionUri(), true,
                    contentObserver as ContentObserver
                )
            }
            _photos.postValue(photoRepository.getPhoto())
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
            }
            catch (securityException: SecurityException) {
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