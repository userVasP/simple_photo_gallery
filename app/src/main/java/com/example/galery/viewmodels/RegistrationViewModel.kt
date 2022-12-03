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
import com.example.galery.data.model.Result
import com.example.galery.data.model.User
import com.example.galery.utilities.ObservableViewModel
import com.example.galery.data.model.registration.RegistrationResult
import com.example.galery.data.model.registration.RegistrationFormState
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(private val photoRepository: PhotoRepository): ObservableViewModel() {
    private val _registrationResultEvent = MutableLiveData<Event<RegistrationResult>>()
    val registrationResultEvent: LiveData<Event<RegistrationResult>> = _registrationResultEvent

    private  val _loadingVisibility = MutableLiveData(View.GONE)
    val loadingVisibility: LiveData<Int> = _loadingVisibility

    private val _userData = MutableLiveData(User("","","",""))
    val userData: LiveData<User> = _userData

    var userName: String = ""
        @Bindable get
        set(value) {
            if (field != value) {
                field = value
                _userData.value = User(value, _userData.value!!.Surname, _userData.value!!.Email, _userData.value!!.Password )
                notifyPropertyChanged(BR.userName)
            }
        }
    var userSurName: String = ""
        @Bindable get
        set(value) {
            if (field != value) {
                field = value
                _userData.value = User(_userData.value!!.Name, value,_userData.value!!.Email, _userData.value!!.Password )
                notifyPropertyChanged(BR.userSurName)
            }
        }
    var userEmail: String = ""
        @Bindable get
        set(value) {
            if (field != value) {
                field = value
                _userData.value = User(_userData.value!!.Name, _userData.value!!.Surname, value, _userData.value!!.Password )
                notifyPropertyChanged(BR.userEmail)
            }
        }
    var userPassword: String = ""
        @Bindable get
        set(value) {
            if (field != value) {
                field = value
                _userData.value = User(_userData.value!!.Name, _userData.value!!.Surname, _userData.value!!.Email, value)
                notifyPropertyChanged(BR.userPassword)
            }
        }

    private val _navigateToLoginEvent = MutableLiveData<Event<Unit>>()
    val navigateToRegistrationEvent: LiveData<Event<Unit>> = _navigateToLoginEvent

    private val _registrationForm = MutableLiveData<RegistrationFormState>()
    val registrationForm: LiveData<RegistrationFormState> = _registrationForm

    fun registration() {
        _loadingVisibility.value = View.VISIBLE
        viewModelScope.launch {
            val result = _userData.value?.let {
                photoRepository.registration(it)
            }
            result.let {
                if (result is Result.Success) {
                    //_registrationResultEvent.value = Event(RegistrationResult(success = true))
                    _navigateToLoginEvent.value = Event(Unit)
                } else {
                    _registrationResultEvent.value = Event(RegistrationResult(error = R.string.registration_failed))
                }
            }
            _loadingVisibility.value = View.GONE
        }
    }

    fun loginDataChanged() {
        if(!isUserNameValid(_userData.value!!.Name!!)) {
            _registrationForm.value = RegistrationFormState(usernameError = R.string.empty_field)
        } else if (!isUserNameValid(_userData.value!!.Surname!!)) {
            _registrationForm.value = RegistrationFormState(userSurnameError = R.string.empty_field)
        } else if (!isPasswordValid(_userData.value!!.Password)) {
            _registrationForm.value = RegistrationFormState(passwordError = R.string.invalid_password)
        } else if (!isUserEmailValid(_userData.value!!.Email)) {
            _registrationForm.value = RegistrationFormState(userEmailError = R.string.invalid_email)
        } else {
            _registrationForm.value = RegistrationFormState(isDataValid = true)
        }
    }


    private fun isUserEmailValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }


    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun navigateToLogin() {
        _navigateToLoginEvent.value  = Event(Unit)
    }
}