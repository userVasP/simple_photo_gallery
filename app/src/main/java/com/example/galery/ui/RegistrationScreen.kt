package com.example.galery.ui

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
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
import com.example.galery.viewmodels.RegistrationViewModel

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onRegistration: () -> Unit,
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState
) {

    val registrationResult = registrationViewModel.registrationResult.observeAsState()
    val visibleProgressBar = registrationViewModel.loadingVisibility.observeAsState()
    val registrationFormState = registrationViewModel.registrationForm.observeAsState()
    val isErrorName: Boolean = registrationFormState.value?.usernameError != null
    val isErrorSurName: Boolean = registrationFormState.value?.userSurnameError != null
    val isErrorPassword: Boolean = registrationFormState.value?.passwordError != null
    val isErrorEmail: Boolean = registrationFormState.value?.userEmailError != null


    if(registrationResult.value?.success != null && registrationResult.value?.success!!) {
        LaunchedEffect(true) {
            registrationViewModel.resetRegistrationResult()
            onRegistration()
        }
    }

    if(registrationResult.value?.error != null && !registrationResult.value?.success!!) {
        val error = stringResource(id = registrationResult.value?.error!!)

        LaunchedEffect(true) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = error
            )
            registrationViewModel.resetRegistrationResult()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val userName = registrationViewModel.userName.observeAsState()
        userName.value?.let {
            TextFieldWithChecking(
                text =  it,
                idPlaceholderString = R.string.prompt_name,
                onValueChange = registrationViewModel::setUserName,
                isError = isErrorName,
                idError = registrationFormState.value?.usernameError
            )
        }

        val userSurName = registrationViewModel.userSurName.observeAsState()
        userSurName.value?.let {
            TextFieldWithChecking(
                text =  it,
                idPlaceholderString = R.string.prompt_surname,
                onValueChange = registrationViewModel::setUserSurName,
                isError = isErrorSurName,
                idError = registrationFormState.value?.userSurnameError
            )
        }

        val password = registrationViewModel.userPassword.observeAsState()
        password.value?.let {
            TextFieldWithChecking(
                text =  it,
                idPlaceholderString = R.string.prompt_password,
                onValueChange = registrationViewModel::setUserPassword,
                isError = isErrorPassword,
                idError = registrationFormState.value?.passwordError
            )
        }

        val email = registrationViewModel.userEmail.observeAsState()
        email.value?.let {
            TextFieldWithChecking(
                text =  it,
                idPlaceholderString = R.string.prompt_email,
                onValueChange = registrationViewModel::setUserEmail,
                isError = isErrorEmail,
                idError = registrationFormState.value?.userEmailError
            )
        }

        Button(
            enabled = registrationFormState.value?.isDataValid ?: false,
            onClick = { registrationViewModel.registration() }
        ) {
            Text(text = stringResource(id = R.string.registration))
        }
        Text(
            text = stringResource(id = R.string.login),
            color = Color.Blue,
            modifier = Modifier.clickable {
                onLoginClick()
            }
        )
        if (visibleProgressBar.value == View.VISIBLE) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
fun RegistrationScreenPreview() {
    GalleryTheme {
        RegistrationScreen(onLoginClick = {}, onRegistration = {}, scaffoldState = rememberScaffoldState())
    }
}