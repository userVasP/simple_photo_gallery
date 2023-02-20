package com.example.galery.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.galery.R
import com.example.galery.data.model.Photo
import com.example.galery.ui.theme.GalleryTheme

@Composable
fun GalleryElement(
    photo: Photo,
    modifier: Modifier = Modifier,
    onPhotoClick: (Photo) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        AsyncImage(
            model = photo.uri,
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(200.dp).clickable {
                onPhotoClick(photo)
            }
        )

        Text(
            text = "${(stringResource(R.string.shooting_time))} ${photo.date}",
            modifier = Modifier.paddingFromBaseline(
                top = 24.dp, bottom = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGalleryElement() {
    GalleryTheme {
        GalleryElement(
            Photo(0, Uri.EMPTY,"1","01.01.2022"),
            onPhotoClick = {}
        )
    }
}