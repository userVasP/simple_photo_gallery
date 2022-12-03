package com.example.galery.di

import com.example.galery.views.RegistrationFragment
import dagger.Subcomponent

@Subcomponent(modules = [RegistrationModule::class])
interface RegistrationComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): RegistrationComponent
    }

    fun inject(fragment: RegistrationFragment)
}