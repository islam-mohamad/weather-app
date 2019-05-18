package com.islam.weatherapp.dagger.modules

import androidx.lifecycle.ViewModelProviders
import com.islam.weatherapp.dagger.ActivityScoped
import com.islam.weatherapp.dagger.FragmentScoped
import com.islam.weatherapp.domain.DatabaseAdapter
import com.islam.weatherapp.features.cities.model.Api
import com.islam.weatherapp.features.cities.view.ui.HomeActivity
import com.islam.weatherapp.features.cities.view.ui.HomeFragment
import com.islam.weatherapp.features.cities.viewmodel.CitiesViewModel
import com.islam.weatherapp.features.cities.viewmodel.CitiesViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import retrofit2.Retrofit


@Module
abstract class CitiesModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun homeFragment(): HomeFragment


    @Module
    companion object {

        @JvmStatic
        @ActivityScoped
        @Provides
        fun citiesViewModel(homeActivity: HomeActivity, factory: CitiesViewModelFactory) =
            ViewModelProviders.of(homeActivity, factory).get(CitiesViewModel::class.java)

        @JvmStatic
        @ActivityScoped
        @Provides
        fun citiesTable(homeActivity: HomeActivity) = DatabaseAdapter.getDataBase(homeActivity).citiesTable

        @JvmStatic
        @ActivityScoped
        @Provides
        fun favoriteCityIdsTable(homeActivity: HomeActivity) =
            DatabaseAdapter.getDataBase(homeActivity).favoriteCityIdsTable

        @JvmStatic
        @ActivityScoped
        @Provides
        fun forcastApi(retrofit: Retrofit) = retrofit.create(Api::class.java)
    }


}