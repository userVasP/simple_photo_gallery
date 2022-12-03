package com.example.galery.views

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.galery.utilities.EventObserver
import com.example.galery.GalleryApplication
import com.example.galery.R
import com.example.galery.databinding.FragmentRegistrationBinding
import com.example.galery.viewmodels.RegistrationViewModel
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


class RegistrationFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<RegistrationViewModel> { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as GalleryApplication).appComponent.registrationComponent()
            .create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        viewModel.userData.observe(viewLifecycleOwner) {
            viewModel.loginDataChanged()
        }

        viewModel.registrationResultEvent.observe(viewLifecycleOwner, Observer {
            val registrationResult = it.getContentIfNotHandled() ?:  return@Observer
            if (registrationResult.error != null) {
                view?.let { view ->
                    val snackBar = Snackbar.make(view, getString(registrationResult.error), Snackbar.LENGTH_SHORT)
                    snackBar.setTextColor(resources.getColor(R.color.red_500))
                    snackBar.setBackgroundTint(resources.getColor(R.color.grey_500))
                        .show()
                }
            }

        })

        setupNavigation()

        return binding.root
    }

    private fun setupNavigation() {
        viewModel.navigateToRegistrationEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    R.id.action_registrationFragment_to_loginFragment,
                    null)
            }
        )
    }
}