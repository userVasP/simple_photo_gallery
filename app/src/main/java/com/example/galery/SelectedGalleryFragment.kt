package com.example.galery

import com.example.galery.data.Photo

class SelectedGalleryFragment: GalleryFragment() {
    override fun observePhoto() {
        viewModel.favoritePhotos.observe(viewLifecycleOwner) { photo ->
            photo?.let {
                pictureRecyclerViewAdapter.submitList(it as MutableList<Photo>)
            }
        }
    }
}