package com.islam.weatherapp.features.home.view.ui

import android.os.Bundle
import com.islam.weatherapp.R
import com.islam.weatherapp.base.BaseActivity
import com.islam.weatherapp.databinding.ActivityHomeBinding
import javax.inject.Inject

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    @Inject
    lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .add(R.id.homeFragment, homeFragment, HomeFragment::class.java.name).commit()
    }

    override fun getLayoutId(): Int = R.layout.activity_home

}
