package com.example.galery.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.galery.R
import com.example.galery.data.model.Photo
import com.example.galery.viewmodels.DetailScreenViewModel

@Composable
fun GalleryDetailScreen(
    photoUri: String,
    photoKey: String,
    modifier: Modifier = Modifier,
    removePhoto: () -> Unit,
    model: DetailScreenViewModel = hiltViewModel()
) {
    val photos = model.favoritePhotos.observeAsState()
    val isCurrentPhotoFavorite = model.isCurrentPhotoFavorite.observeAsState()
    LaunchedEffect(photos.value) {
        model.checkPhoto(photoKey)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        AsyncImage(
            model = Uri.parse(photoUri),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .weight(9F)
        )

        Row(
            modifier = modifier
                .fillMaxSize()
                .weight(1F),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    removePhoto()
                },
                modifier.weight(1F)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.photo_deletion),
                    modifier = modifier.fillMaxSize())
            }

            IconToggleButton(
                checked = isCurrentPhotoFavorite.value ?:false,
                onCheckedChange = {
                    if (it) {
                        model.addFavoritePhoto(photoKey)
                    }
                    else {
                        model.removeFavoritePhoto(photoKey)
                    }
                },

                modifier.weight(1F)
            ) {
                val imageVector = if(isCurrentPhotoFavorite.value == true) { Icons.Filled.Favorite} else { Icons.Filled.FavoriteBorder }
                Icon(
                    imageVector = imageVector,
                    contentDescription = stringResource(R.string.favorite_photo),
                    modifier = modifier.fillMaxSize())
            }
        }
    }
}

/*@Preview
@Composable
fun GalleryDetailScreenPreview() {
    GalleryTheme {
        GalleryDetailScreen(Uri.EMPTY.toString())
    }
}*/