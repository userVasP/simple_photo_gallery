package com.example.galery.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.galery.data.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(private val photoRepository: PhotoRepository): ViewModel() {

    private val disposable: CompositeDisposable = CompositeDisposable()

    private val _navigateToLoginEvent = MutableLiveData(false)
    val navigateToLoginEvent: LiveData<Boolean> = _navigateToLoginEvent

    private  val _loadingVisibility = MutableLiveData(View.GONE)
    val loadingVisibility: LiveData<Int> = _loadingVisibility

    private  val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    fun checkLogin() {
        if(photoRepository.isLoggedIn) {
            _userName.value = photoRepository.user!!.displayName
        }
    }

    fun sendPhotoToServer() {
        _loadingVisibility.value = View.VISIBLE

        disposable.add(photoRepository.sendPhotoToServer()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                _loadingVisibility.value = View.GONE }
            .subscribe())
    }

    fun loadPhotoFromServer() {
        _loadingVisibility.value = View.VISIBLE

        disposable.add(photoRepository.loadPhotoFromServer()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                _loadingVisibility.value = View.GONE }
            .subscribe())
    }

    fun logout() {
        _loadingVisibility.value = View.VISIBLE

        disposable.add(photoRepository.logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate { _loadingVisibility.value = View.GONE
                _navigateToLoginEvent.value = true }
            .subscribe())
    }

    override fun onCleared() {
        disposable.clear()
    }

}