package com.example.galery.viewmodels

import androidx.lifecycle.*
import com.example.galery.data.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(private val photoRepository: PhotoRepository) : ViewModel() {
    private var _isCurrentPhotoFavorite = MutableLiveData<Boolean>()
    val isCurrentPhotoFavorite: LiveData<Boolean> = _isCurrentPhotoFavorite

    private val _favoritePhotos = photoRepository.getFavoritePhotoDataStream().asLiveData()
    val favoritePhotos = _favoritePhotos

    fun checkPhoto(key: String) {
        _isCurrentPhotoFavorite.value = (_favoritePhotos.value?.find { it.getKey() == key }) != null
    }

    fun addFavoritePhoto(key: String) {
        viewModelScope.launch {
            photoRepository.insertFavoritePhoto(key)
        }
    }

    fun removeFavoritePhoto(key: String) {
        viewModelScope.launch {
            photoRepository.deleteFavoritePhoto(key)
        }
    }
}