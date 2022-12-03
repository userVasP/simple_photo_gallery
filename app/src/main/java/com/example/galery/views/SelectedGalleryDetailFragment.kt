package com.example.galery.views

import android.view.View
import androidx.navigation.Navigation
import com.example.galery.R

class SelectedGalleryDetailFragment: GaleryDetailFragment() {
    override fun navigateForDeletingPhoto(view: View) {
        Navigation.findNavController(view).navigate(
            R.id.action_galleryDetailFragment_to_selectedGalleryFragment,
            null)
    }
    override fun navigateForDeletingFavoritePhoto(view: View) {
        Navigation.findNavController(view).navigate(
            R.id.action_galleryDetailFragment_to_selectedGalleryFragment,
            null)
    }

}