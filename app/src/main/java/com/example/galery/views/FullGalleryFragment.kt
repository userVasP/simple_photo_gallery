package com.example.galery.views

import com.example.galery.data.model.Photo

class FullGalleryFragment: GalleryFragment() {
    override fun observePhoto() {
        viewModel.photos.observe(viewLifecycleOwner) { photo ->
            photo?.let {
                pictureRecyclerViewAdapter.submitList(it as MutableList<Photo>)
            }
        }

    }
}