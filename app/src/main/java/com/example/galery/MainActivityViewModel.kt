package com.example.galery

import android.content.Context
import android.database.ContentObserver
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
    var isFirstLoadedPhoto = false
    private var _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photos
    private val contentResolver = context.contentResolver
    private var contentObserver: ContentObserver? = null

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

    override fun onCleared() {
        contentObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
    }
}