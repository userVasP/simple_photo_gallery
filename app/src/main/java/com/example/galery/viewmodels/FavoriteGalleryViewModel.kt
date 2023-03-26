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
class FavoriteGalleryViewModel @Inject constructor (photoRepository: PhotoRepository): ViewModel() {
    private val disposable = CompositeDisposable()
    private val _favoritePhotos = MutableLiveData<List<Photo>>()
    val favoritePhotos: LiveData<List<Photo>> = _favoritePhotos

    init {
        disposable.add(photoRepository.getFavoritePhotoDataStream()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ _favoritePhotos.value = it})
    }

    override fun onCleared() {
        disposable.clear()
    }
}