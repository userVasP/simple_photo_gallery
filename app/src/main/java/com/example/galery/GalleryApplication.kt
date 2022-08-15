package com.example.galery

import android.app.Application
import com.example.galery.di.AppComponent
import com.example.galery.di.DaggerAppComponent

open class GalleryApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}