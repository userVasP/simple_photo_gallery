package com.example.galery.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galery.Event
import com.example.galery.data.PhotoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ServerViewModel @Inject constructor(private val photoRepository: PhotoRepository): ViewModel() {

    private val _navigateToLoginEvent = MutableLiveData<Event<Unit>>()
    val navigateToLoginEvent: LiveData<Event<Unit>> = _navigateToLoginEvent

    private  val _loadingVisibility = MutableLiveData(View.GONE)
    val loadingVisibility: LiveData<Int> = _loadingVisibility

    private  val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    fun checkLogin() {
        if(!photoRepository.isLoggedIn) {
            _navigateToLoginEvent.value = Event(Unit)
        }
        else {
            _userName.value = photoRepository.user!!.displayName
        }
    }

    fun sendPhotoToServer() {
        _loadingVisibility.value = View.VISIBLE
        viewModelScope.launch {
            photoRepository.sendPhotoToServer()
            _loadingVisibility.value = View.GONE
        }
    }

    fun loadPhotoFromServer() {
        _loadingVisibility.value = View.VISIBLE
        viewModelScope.launch {
            photoRepository.loadPhotoFromServer()
            _loadingVisibility.value = View.GONE
        }
    }

    fun logout() {
        viewModelScope.launch {
            photoRepository.logout()
            _navigateToLoginEvent.value = Event(Unit)
        }
    }

}