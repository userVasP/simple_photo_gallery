package com.example.galery.di

import android.content.Context
import com.example.galery.views.MainActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ViewModelBuilderModule::class,
    DataBaseModule::class,
    NetworkModule::class,
    SubcomponentsModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context: Context): AppComponent
    }

    fun loginComponent(): LoginComponent.Factory
    fun registrationComponent(): RegistrationComponent.Factory
    fun serverComponent(): ServerComponent.Factory

    fun inject(activity: MainActivity)
}

@Module(subcomponents = [LoginComponent::class, RegistrationComponent::class, ServerComponent::class])
object SubcomponentsModule