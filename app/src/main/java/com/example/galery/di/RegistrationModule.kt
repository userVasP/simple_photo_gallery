package com.example.galery.di

import androidx.lifecycle.ViewModel
import com.example.galery.viewmodels.RegistrationViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class RegistrationModule {
    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    abstract fun bindViewModel(viewModel: RegistrationViewModel): ViewModel
}