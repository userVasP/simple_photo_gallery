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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonGalleryViewModel @Inject constructor (private val photoRepository: PhotoRepository, @ApplicationContext context: Context) : ViewModel() {

    private val disposable = CompositeDisposable()

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
                        disposable.add(photoRepository.getPhoto(true)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ _photo.postValue(it) }, { }))
                    }
                }
                contentResolver.registerContentObserver(
                    CollectionUri.getCollectionUri(), true,
                    contentObserver as ContentObserver
                )
            }
            disposable.add(photoRepository.getPhoto(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _photo.postValue(it) }, { }))
        }
    }

    override fun onCleared() {
        contentObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
        disposable.clear()
    }
}