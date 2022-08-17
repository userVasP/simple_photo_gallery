package com.example.galery

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.galery.adapters.bindImageFromUrl
import com.example.galery.databinding.FragmentGaleryDetailBinding


class GaleryDetailFragment : Fragment() {
    private val viewModel by activityViewModels<MainActivityViewModel> { (activity as MainActivity).viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val uriString = arguments?.getString("photoUri")

        val binding = FragmentGaleryDetailBinding.inflate(inflater, container, false)
        context ?: return binding.root

        with(binding.selectedPhoto) {
            this.setImageURI(Uri.parse(uriString))
        }

        return binding.root

    }

}