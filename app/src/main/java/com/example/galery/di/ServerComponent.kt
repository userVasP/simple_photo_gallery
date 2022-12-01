package com.example.galery.di

import com.example.galery.ServerFragment
import dagger.Subcomponent

@Subcomponent(modules = [ServerModule::class])
interface ServerComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ServerComponent
    }

    fun inject(fragment: ServerFragment)
}