package com.islam.weatherapp.dagger.modules

import com.islam.weatherapp.dagger.FragmentScoped
import com.islam.weatherapp.features.home.view.ui.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class HomeModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun homeFragment(): HomeFragment


    /*@Module
    companion object {

        @JvmStatic
        @ActivityScoped
        @Provides
        fun productsViewModel(homeActivity: HomeActivity, factory: ProductViewModelFactory) =
            ViewModelProviders.of(homeActivity, factory).get(ProductViewModel::class.java)

        @JvmStatic
        @ActivityScoped
        @Provides
        fun productService(retrofit: Retrofit) = retrofit.create(ProductService::class.java)!!

        @JvmStatic
        @ActivityScoped
        @Provides
        fun productDao(homeActivity: HomeActivity) = ProductDataBase.getDatabase(homeActivity).productDao()
    }*/


}