package com.example.galery

import com.example.galery.data.Photo

class FullGalleryFragment: GalleryFragment() {
    override fun observePhoto() {
        viewModel.photos.observe(viewLifecycleOwner) { photo ->
            photo?.let {
                pictureRecyclerViewAdapter.submitList(it as MutableList<Photo>)
            }
        }

    }
}