package com.example.galery.registration

data class RegistrationFormState(
    val usernameError: Int? = null,
    val userSurnameError: Int? = null,
    val userEmailError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)