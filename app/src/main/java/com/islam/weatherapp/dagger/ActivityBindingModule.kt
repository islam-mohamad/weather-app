package com.islam.weatherapp.dagger

import com.islam.weatherapp.features.cities.view.ui.HomeActivity
import com.islam.weatherapp.dagger.modules.CitiesModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [CitiesModule::class])
    abstract fun bindHomeActivity(): HomeActivity
}