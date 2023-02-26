package com.example.galery.viewmodels

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galery.data.CollectionUri
import com.example.galery.data.PhotoRepository
import com.example.galery.data.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonGalleryViewModel @Inject constructor (private val photoRepository: PhotoRepository, @ApplicationContext context: Context) : ViewModel() {

    private var _photo = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photo
    private val contentResolver = context.contentResolver
    private var contentObserver: ContentObserver? = null


    init {
        getPhoto()
    }

    private fun getPhoto() {
        viewModelScope.launch {
            if (contentObserver == null) {
                contentObserver = object : ContentObserver(Handler()) {
                    override fun onChange(selfChange: Boolean) {
                        viewModelScope.launch {
                            _photo.postValue(photoRepository.getPhoto(true))
                        }
                    }
                }
                contentResolver.registerContentObserver(
                    CollectionUri.getCollectionUri(), true,
                    contentObserver as ContentObserver
                )
            }
            _photo.postValue(photoRepository.getPhoto())
        }
    }

    override fun onCleared() {
        contentObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
    }
}