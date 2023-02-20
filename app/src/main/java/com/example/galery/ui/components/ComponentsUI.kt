package com.example.galery.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.galery.R

@Composable
fun TextFieldWithChecking(
    text: String,
    @StringRes idPlaceholderString: Int,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    @StringRes idError: Int?
) {
    Column {
        TextField(
            value = text,
            onValueChange = onValueChange,
            placeholder = {
                Text(stringResource(idPlaceholderString))
            },
            trailingIcon = {
                if (isError)
                    Icon(Icons.Filled.Error,"error", tint = MaterialTheme.colors.error)
            },
            singleLine = true,
            isError = isError
        )
        if (isError) {
            Text(
                text = stringResource(idError!!),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}