package com.example.galery.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.galery.data.PhotoRepository
import com.example.galery.data.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteGalleryViewModel @Inject constructor (photoRepository: PhotoRepository): ViewModel() {
    private val _favoritePhotos = photoRepository.getFavoritePhotoDataStream().asLiveData()
    val favoritePhotos: LiveData<List<Photo>> = _favoritePhotos
}