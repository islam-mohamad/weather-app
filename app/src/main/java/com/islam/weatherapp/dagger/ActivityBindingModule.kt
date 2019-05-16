package com.islam.weatherapp.dagger

import com.islam.weatherapp.features.home.view.ui.HomeActivity
import com.islam.weatherapp.dagger.modules.HomeModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [HomeModule::class])
    abstract fun bindHomeActivity(): HomeActivity
}