package com.example.galery.ui

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
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
import com.example.galery.ui.theme.GalleryTheme
import com.example.galery.viewmodels.ServerViewModel

@Composable
fun ServerScreen(
    modifier: Modifier = Modifier,
    serverViewModel: ServerViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {

    val userName = serverViewModel.userName.observeAsState()
    val isLogout = serverViewModel.navigateToLoginEvent.observeAsState()
    val visibleProgressBar = serverViewModel.loadingVisibility.observeAsState()

    LaunchedEffect(Unit) {
        serverViewModel.checkLogin()
    }

    if (isLogout.value == true) {
        LaunchedEffect(Unit) {
            onLogout()
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = Modifier,
                text = stringResource(id = R.string.logged_in_description))
            userName.value?.let { Text(text = it) }
        }

        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { serverViewModel.sendPhotoToServer() }) {
                Text(text = stringResource(id = R.string.send_photo_to_server))
            }
            Button(onClick = { serverViewModel.loadPhotoFromServer() }) {
                Text(text = stringResource(id = R.string.load_photo_from_server))
            }
        }

        Text(
            text = stringResource(id = R.string.logout),
            color = Color.Blue,
            modifier = Modifier.clickable {
                serverViewModel.logout()
            }
        )
        if (visibleProgressBar.value == View.VISIBLE) {
            CircularProgressIndicator()
        }
    }
}

@Preview()
@Composable
fun PreviewServerScreen() {
    GalleryTheme {
        ServerScreen( onLogout = {})
    }
}