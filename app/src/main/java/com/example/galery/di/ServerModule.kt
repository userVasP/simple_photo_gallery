package com.example.galery.di

import androidx.lifecycle.ViewModel
import com.example.galery.viewmodels.ServerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ServerModule {
    @Binds
    @IntoMap
    @ViewModelKey(ServerViewModel::class)
    abstract fun bindViewModel(viewModel: ServerViewModel): ViewModel
}