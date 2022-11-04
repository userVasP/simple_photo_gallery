package com.example.galery

import android.view.View
import androidx.navigation.Navigation

class FullGalleryDetailFragment: GaleryDetailFragment() {
    override fun navigateForDeletingPhoto(view: View) {
        Navigation.findNavController(view).navigate(
            R.id.action_galleryDetailFragment_to_galleryFragment,
            null)
    }
}