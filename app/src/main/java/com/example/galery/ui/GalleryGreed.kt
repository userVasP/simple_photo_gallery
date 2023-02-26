package com.example.galery.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.galery.data.model.Photo
import com.example.galery.ui.theme.GalleryTheme
import com.example.galery.viewmodels.CommonGalleryViewModel

@Composable
fun GalleryGreed(
    modifier: Modifier = Modifier,
    photos: List<Photo>,
    onPhotoClick: (Photo) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = photos) { item ->
            GalleryElement(
                item,
                modifier = Modifier.height(250.dp),
                onPhotoClick = onPhotoClick
            )
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun GalleryGreedPreview() {
    GalleryTheme {
        GalleryGreed(onPhotoClick = {} )
    }

}*/
