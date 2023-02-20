package com.example.galery.viewmodels

import android.util.Patterns
import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galery.BR
import com.example.galery.utilities.Event
import com.example.galery.R
import com.example.galery.data.PhotoRepository
import com.example.galery.data.model.Result
import com.example.galery.data.model.User
import com.example.galery.data.model.login.LoginResult
import com.example.galery.utilities.ObservableViewModel
import com.example.galery.data.model.registration.RegistrationResult
import com.example.galery.data.model.registration.RegistrationFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val photoRepository: PhotoRepository): ViewModel() {
    private val _registrationResult = MutableLiveData<RegistrationResult>()
    val registrationResult: LiveData<RegistrationResult> = _registrationResult

    private  val _loadingVisibility = MutableLiveData(View.GONE)
    val loadingVisibility: LiveData<Int> = _loadingVisibility

    private val _userData = MutableLiveData(User("","","",""))
    val userData: LiveData<User> = _userData

    private val _userName = MutableLiveData("")
    val userName = _userName
    fun setUserName(userName: String) {
        _userName.value = userName
        _userData.value = User(userName, _userData.value!!.Surname, _userData.value!!.Email, _userData.value!!.Password )
        loginDataChanged()
    }
    private val _userSurName = MutableLiveData("")
    val userSurName = _userSurName
    fun setUserSurName(userSurName: String) {
        _userSurName.value = userSurName
        _userData.value = User(_userData.value!!.Name, userSurName, _userData.value!!.Email, _userData.value!!.Password )
        loginDataChanged()
    }

    private val _userEmail = MutableLiveData("")
    val userEmail = _userEmail
    fun setUserEmail(userEmail: String) {
        _userEmail.value = userEmail
        _userData.value = User(_userData.value!!.Name, _userData.value!!.Surname, userEmail, _userData.value!!.Password )
        loginDataChanged()
    }

    private val _userPassword = MutableLiveData("")
    val userPassword = _userPassword
    fun setUserPassword(userPassword: String) {
        _userPassword.value = userPassword
        _userData.value = User(_userData.value!!.Name, _userData.value!!.Surname, _userData.value!!.Email, userPassword)
        loginDataChanged()
    }

    private val _registrationForm = MutableLiveData<RegistrationFormState>()
    val registrationForm: LiveData<RegistrationFormState> = _registrationForm

    fun registration() {
        _loadingVisibility.value = View.VISIBLE
        viewModelScope.launch {
            delay(5000)
            val result = _userData.value?.let {
                //photoRepository.registration(it)
                Result.Success("") //TODO
            }
            result.let {
                if (result is Result.Success) {
                    _registrationResult.value = RegistrationResult(success = true)
                } else {
                    _registrationResult.value = RegistrationResult(error = R.string.registration_failed)
                }
            }
            _loadingVisibility.value = View.GONE
        }
    }

    private fun loginDataChanged() {
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

    fun resetRegistrationResult() {
        _registrationResult.value = RegistrationResult()
    }
}