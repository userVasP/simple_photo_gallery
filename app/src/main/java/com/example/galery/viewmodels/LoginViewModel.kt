package com.example.galery.viewmodels

import android.util.Patterns
import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.galery.BR
import com.example.galery.utilities.Event
import com.example.galery.R
import com.example.galery.data.PhotoRepository
import com.example.galery.data.model.User
import com.example.galery.utilities.ObservableViewModel
import com.example.galery.data.model.Result
import com.example.galery.data.model.login.LoggedInUserView
import com.example.galery.data.model.login.LoginFormState
import com.example.galery.data.model.login.LoginResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val photoRepository: PhotoRepository): ObservableViewModel() {
    private val _loginResultEvent = MutableLiveData<Event<LoginResult>>()
    val loginResultEvent: LiveData<Event<LoginResult>> = _loginResultEvent

    private  val _loadingVisibility = MutableLiveData(View.GONE)
    val loadingVisibility: LiveData<Int> = _loadingVisibility

    private val _userData = MutableLiveData(User("","","",""))
    val userData: LiveData<User> = _userData

    var userName: String = ""
        @Bindable get
        set(value) {
            if (field != value) {
                field = value
                _userData.value = User(null, null, value, _userData.value!!.Password )
                notifyPropertyChanged(BR.userName)
            }
        }
    var userPassword: String = ""
        @Bindable get
        set(value) {
            if (field != value) {
                field = value
                _userData.value = User(null, null, _userData.value!!.Email, value)
                notifyPropertyChanged(BR.userPassword)
            }
        }

    private val _navigateToRegistrationEvent = MutableLiveData<Event<Unit>>()
    val navigateToRegistrationEvent: LiveData<Event<Unit>> = _navigateToRegistrationEvent

    private val _navigateToServerEvent = MutableLiveData<Event<Unit>>()
    val navigateToServerEvent: LiveData<Event<Unit>> = _navigateToServerEvent

    fun checkLogin() {
        if (photoRepository.isLoggedIn) {
            _navigateToServerEvent.value = Event(Unit)
        }
    }

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    fun login() {
        _loadingVisibility.value = View.VISIBLE
        viewModelScope.launch {
            val result = _userData.value?.let {
                photoRepository.login(it)
            }
            if(result != null) {
                if (result is Result.Success) {
                    _loginResultEvent.value = Event(LoginResult(success = LoggedInUserView(displayName = result.data.displayName)))
                } else {
                    _loginResultEvent.value = Event(LoginResult(error = R.string.login_failed))
                }
            }
            _loadingVisibility.value = View.GONE
        }
    }

    fun loginDataChanged() {
        if (!isUserEmailValid(_userData.value!!.Email)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_email)
        } else if (!isPasswordValid(_userData.value!!.Password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.empty_field)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserEmailValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank()
    }

    fun navigateToRegistration() {
        _navigateToRegistrationEvent.value  = Event(Unit)
    }
}