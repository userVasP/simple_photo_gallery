package com.example.galery.ui

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.galery.R
import com.example.galery.ui.components.TextFieldWithChecking
import com.example.galery.ui.theme.GalleryTheme
import com.example.galery.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onRegistrationClick: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLogin: () -> Unit,
    scaffoldState: ScaffoldState
)
{

    val loginResult = loginViewModel.loginResult.observeAsState()
    val visibleProgressBar = loginViewModel.loadingVisibility.observeAsState()
    val loginFormState = loginViewModel.loginFormState.observeAsState()
    val isErrorLogin: Boolean = loginFormState.value?.usernameError != null
    val isErrorPassword: Boolean = loginFormState.value?.passwordError != null

    if(loginResult.value?.success != null) {
        LaunchedEffect(true) {
            loginViewModel.resetLoginResult()
            onLogin()
        }
    }

    if(loginResult.value?.error != null) {
        val error = stringResource(id = loginResult.value?.error!!)

        LaunchedEffect(true) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = error
            )
            loginViewModel.resetLoginResult()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val userName = loginViewModel.userName.observeAsState()
        userName.value?.let {
            TextFieldWithChecking(
                it,
                R.string.prompt_email,
                loginViewModel::setUserName,
                isErrorLogin,
                loginFormState.value?.usernameError
            )
        }
        val userPassword = loginViewModel.userPassword.observeAsState()
        userPassword.value?.let {
            TextFieldWithChecking(it,
                R.string.prompt_password,
                loginViewModel::setUserPassword,
                isErrorPassword,
                loginFormState.value?.passwordError
            )
        }
        Button(
            enabled = loginFormState.value?.isDataValid ?: false,
            onClick = { loginViewModel.login() }
        ) {
            Text(text = stringResource(id = R.string.action_sign_in_short))
        }
        Text(
            text = stringResource(id = R.string.registration),
            color = Color.Blue,
            modifier = Modifier.clickable {
                onRegistrationClick()
            }
        )
        if (visibleProgressBar.value == View.VISIBLE) {
            CircularProgressIndicator()
        }

    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    GalleryTheme {
        LoginScreen(
            onRegistrationClick = {},
            onLogin = {},
            scaffoldState = rememberScaffoldState()
        )
    }
}