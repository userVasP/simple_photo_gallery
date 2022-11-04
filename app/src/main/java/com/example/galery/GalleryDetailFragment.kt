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
import com.example.galery.utilities.Constants


abstract class GaleryDetailFragment : Fragment() {
    private val viewModel by activityViewModels<MainActivityViewModel> { (activity as MainActivity).viewModelFactory }
    private lateinit var _uriString : String
    private lateinit var _keyBD: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _uriString = arguments?.getString(Constants.PHOTO_URI)!!
        _keyBD = arguments?.getString(Constants.PHOTO_KEY)!!

        val binding = FragmentGaleryDetailBinding.inflate(inflater, container, false)
        context ?: return binding.root

        with(binding.selectedPhoto) {
            this.setImageURI(Uri.parse(_uriString))
        }

        binding.deleteBtn.setOnClickListener {
            viewModel.deleteChosenPhoto(Uri.parse(_uriString))
            navigateForDeletingPhoto(binding.root)
        }

        viewModel.isCurrentPhotoFavorite.observe(viewLifecycleOwner) {
            binding.favoriteBtn.isChecked = it

            binding.favoriteBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.addFavoritePhoto(_keyBD)
                }
                else {
                    viewModel.removeFavoritePhoto(_keyBD)
                    navigateForDeletingFavoritePhoto(binding.root)
                }
            }
            binding.favoriteBtn.isEnabled = true
        }
        viewModel.checkPhoto(_keyBD)
        binding.favoriteBtn.isEnabled = false



        return binding.root

    }
    
    abstract fun navigateForDeletingPhoto(view: View);
    open fun navigateForDeletingFavoritePhoto(view: View) {

    }
}