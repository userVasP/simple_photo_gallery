package com.example.galery.views

import com.example.galery.data.model.Photo

class SelectedGalleryFragment: GalleryFragment() {
    override fun observePhoto() {
        viewModel.favoritePhotos.observe(viewLifecycleOwner) { photo ->
            photo?.let {
                pictureRecyclerViewAdapter.submitList(it as MutableList<Photo>)
            }
        }
    }
}