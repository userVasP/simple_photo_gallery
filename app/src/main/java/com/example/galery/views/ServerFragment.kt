package com.example.galery.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.galery.utilities.EventObserver
import com.example.galery.GalleryApplication
import com.example.galery.R
import com.example.galery.databinding.FragmentServerBinding
import com.example.galery.viewmodels.ServerViewModel
import javax.inject.Inject


class ServerFragment : Fragment() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<ServerViewModel> { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as GalleryApplication).appComponent.serverComponent()
            .create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentServerBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupNavigation()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkLogin()
    }

    private fun setupNavigation() {
        viewModel.navigateToLoginEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    R.id.action_serverFragment_to_loginFragment,
                    null)
            }
        )
    }
}