package com.islam.weatherapp.features.home.view.ui


import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.islam.weatherapp.R
import com.islam.weatherapp.base.BaseFragment
import com.islam.weatherapp.binding.AppRecyclerViewAdapter
import com.islam.weatherapp.databinding.FragmentHomeBinding
import com.islam.weatherapp.features.details.DetailsActivity
import com.islam.weatherapp.features.home.model.City
import com.islam.weatherapp.features.home.view.callback.CityCallback
import com.islam.weatherapp.features.home.viewmodel.CitiesViewModel
import javax.inject.Inject

class HomeFragment @Inject constructor (): BaseFragment<FragmentHomeBinding, CitiesViewModel>(),
    CityCallback {

    @Inject
    lateinit var viewModel: CitiesViewModel

    override val layoutId: Int = R.layout.fragment_home

    override fun viewModel(): CitiesViewModel {
        return viewModel
    }


    private var mAdapter: AppRecyclerViewAdapter<City>? = null
    private val productsList = ArrayList<City>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel.getCities()
        observeViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }


    private val observerProducts = Observer<List<City>> { products ->
        if (products!!.isNotEmpty()) {
            productsList.clear()
            productsList.addAll(products)
            mAdapter!!.submitList(productsList)
        }
    }


    private fun observeViewModel() {
//        viewModel.getCitiesLiveData().observe(this, observerProducts)
    }

    private fun setupRecyclerView() {
        mAdapter = AppRecyclerViewAdapter(this, object : DiffUtil.ItemCallback<City>() {
            override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem.id == newItem.id
                        && oldItem.name == newItem.name
            }

        })

        mAdapter!!.setItemViewType(R.layout.item_city)
        val recyclerView = mViewDataBinding!!.recyclerViewProducts
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onCityClicked(city: City) {
        val intent = Intent(activity, DetailsActivity::class.java)
        intent.putExtra("product", city)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        } else {
            startActivity(intent)
        }
    }

}

