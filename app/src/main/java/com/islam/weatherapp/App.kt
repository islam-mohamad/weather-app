package com.islam.weatherapp

import android.content.Context
import com.islam.weatherapp.di.AppComponent
import com.islam.weatherapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


class App : DaggerApplication() {


    private var appComponent: AppComponent? = null


    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent.builder().application(this).build()
        return appComponent!!
    }

    companion object {
        fun get(activity: Context) = activity.applicationContext as App
    }

    fun appComponent() = appComponent
}