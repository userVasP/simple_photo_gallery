package com.example.galery

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
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

        binding.deleteBtn.setOnClickListener {
            viewModel.deleteChosenPhoto(Uri.parse(uriString))
            Navigation.findNavController(binding.root).navigate(
                R.id.action_galleryDetailFragment_to_galleryFragment,
                null)
        }

        return binding.root

    }

}