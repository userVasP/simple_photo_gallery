package com.example.galery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galery.adapters.PictureRecyclerViewAdapter
import com.example.galery.data.Photo
import com.example.galery.databinding.FragmentItemListBinding
import kotlinx.coroutines.launch


abstract class GalleryFragment : Fragment() {

    protected val viewModel by activityViewModels<MainActivityViewModel> { (activity as MainActivity).viewModelFactory }
    protected val pictureRecyclerViewAdapter = PictureRecyclerViewAdapter()
    private var columnCount = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentItemListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        with(binding.list) {
            layoutManager = GridLayoutManager(context, columnCount)
            adapter = pictureRecyclerViewAdapter
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.isPermissionGranted.observe(viewLifecycleOwner) { permission ->
            permission?.let {
                if(permission) {
                    observePhoto()
                    if(!viewModel.isFirstLoadedPhoto) {
                        loadPhoto()
                    }
                }
            }
        }
    }

    abstract fun observePhoto()

    private fun loadPhoto() {
        lifecycleScope.launch {
            viewModel.getPhoto()
            viewModel.isFirstLoadedPhoto = true
            }
        }
}