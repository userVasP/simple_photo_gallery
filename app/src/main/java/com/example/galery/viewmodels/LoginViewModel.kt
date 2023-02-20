package com.example.galery.viewmodels

import android.util.Patterns
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.galery.R
import com.example.galery.data.PhotoRepository
import com.example.galery.data.model.LoggedInUser
import com.example.galery.data.model.Result
import com.example.galery.data.model.User
import com.example.galery.data.model.login.LoggedInUserView
import com.example.galery.data.model.login.LoginFormState
import com.example.galery.data.model.login.LoginResult
import com.example.galery.utilities.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val photoRepository: PhotoRepository): ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private  val _loadingVisibility = MutableLiveData<Int>()
    val loadingVisibility: LiveData<Int> = _loadingVisibility

    private val _userData: MutableLiveData<User> = MutableLiveData()


    private val _userName = MutableLiveData("")
    val userName = _userName
    private val _userPassword = MutableLiveData("")
    val userPassword = _userPassword
    fun setUserName(userName: String) {
        _userName.value = userName
        _userData.value = User(null, null, userName, _userData.value?.Password ?: "")
        loginDataChanged()
    }
    fun setUserPassword(userPassword: String) {
        _userPassword.value = userPassword
        _userData.value = User(null, null, _userData.value?.Email  ?: "", userPassword)
        loginDataChanged()
    }

    private val _loginForm: MutableLiveData<LoginFormState> = MutableLiveData()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    fun resetLoginResult() {
        _loginResult.value = LoginResult()
    }

    fun login() {
        _loadingVisibility.value = View.VISIBLE
        viewModelScope.launch {
            delay(5000)
            val result = _userData.value?.let {
                //photoRepository.login(it) TODO
                Result.Success(LoggedInUser("1", "111", "333"))
            }
            if(result != null) {
                if (result is Result.Success) {
                    _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
                } else {
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                }
            }
            //_loginResult.value = LoginResult(error = R.string.login_failed)
            _loadingVisibility.value = View.GONE
        }
    }

    private fun loginDataChanged() {
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

}