package com.example.galery.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.galery.data.PhotoRepository
import com.example.galery.data.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class DetailScreenViewModel @Inject constructor(private val photoRepository: PhotoRepository) : ViewModel() {

    private val disposable = CompositeDisposable()

    private val _isCurrentPhotoFavorite = MutableLiveData<Boolean>()
    val isCurrentPhotoFavorite: LiveData<Boolean> = _isCurrentPhotoFavorite

    private val _favoritePhotos = MutableLiveData<List<Photo>>()
    val favoritePhotos = _favoritePhotos

    init {
        disposable.add(photoRepository.getFavoritePhotoDataStream()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ _favoritePhotos.value = it})
    }

    fun checkPhoto(key: String) {
        _isCurrentPhotoFavorite.value = (_favoritePhotos.value?.find { it.getKey() == key }) != null
    }



    fun addFavoritePhoto(key: String) {
        disposable.add(photoRepository.insertFavoritePhoto(key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }


    fun removeFavoritePhoto(key: String) {
        disposable.add(photoRepository.deleteFavoritePhoto(key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }

    override fun onCleared() {
        disposable.clear()
    }
}