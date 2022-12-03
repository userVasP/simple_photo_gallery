package com.example.galery.views

import android.view.View
import androidx.navigation.Navigation
import com.example.galery.R

class FullGalleryDetailFragment: GaleryDetailFragment() {
    override fun navigateForDeletingPhoto(view: View) {
        Navigation.findNavController(view).navigate(
            R.id.action_galleryDetailFragment_to_galleryFragment,
            null)
    }
}