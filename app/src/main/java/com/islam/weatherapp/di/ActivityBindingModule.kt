package com.islam.weatherapp.di

import com.islam.weatherapp.presentation.cities.view.ui.HomeActivity
import com.islam.weatherapp.di.modules.CitiesModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [CitiesModule::class])
    abstract fun bindHomeActivity(): HomeActivity
}