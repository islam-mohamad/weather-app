package com.islam.weatherapp.features.cities.view.ui


import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.islam.weatherapp.R
import com.islam.weatherapp.base.BaseFragment
import com.islam.weatherapp.binding.AppRecyclerViewAdapter
import com.islam.weatherapp.databinding.DialogCitiesSearchBinding
import com.islam.weatherapp.databinding.FragmentHomeBinding
import com.islam.weatherapp.entities.City
import com.islam.weatherapp.entities.Coordinates
import com.islam.weatherapp.entities.FavoriteCityId
import com.islam.weatherapp.features.cities.view.callback.CityCallback
import com.islam.weatherapp.features.cities.viewmodel.CitiesViewModel
import com.islam.weatherapp.features.weather.WeatherDetailsActivity
import com.islam.weatherapp.utils.RxSearchObservable
import com.islam.weatherapp.utils.ScreenUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeFragment @Inject constructor() : BaseFragment<FragmentHomeBinding, CitiesViewModel>(),
    CityCallback {

    private var isFavCitiesListCompleted: Boolean? = false
    private var searchResultsAdapter: AppRecyclerViewAdapter<City>? = null
    private var alertDialogCities: AlertDialog? =null
    @Inject
    lateinit var viewModel: CitiesViewModel

    override val layoutId: Int = R.layout.fragment_home

    private var alertDialogBuilder: AlertDialog.Builder? = null

    private var dialogBinding: DialogCitiesSearchBinding? = null

    private var disposable: Disposable? = null


    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun viewModel(): CitiesViewModel {
        return viewModel
    }


    private var mAdapter: AppRecyclerViewAdapter<City>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeFavCities()
        observeSearchResults()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewDataBinding().view = this
        setupRecyclerView()
        viewModel.queryFavoriteCities()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
    }


    private val observerFavCities = Observer<List<City>> { cities ->
        mAdapter!!.submitList(cities)
        if (cities.isEmpty()) {
            setDefaultCity()
        } else isFavCitiesListCompleted = cities.size >= 5
    }

    private fun observeFavCities() {
        viewModel.favoriteCities.observe(activity!!, observerFavCities)
    }

    private val observerSearchResults = Observer<List<City>> { cities ->
        searchResultsAdapter!!.submitList(cities)
    }

    private fun observeSearchResults() {
        viewModel.citiesSearchResult.observe(this, observerSearchResults)
    }


    private var onLocationSuccess = OnSuccessListener<Location> { location ->
        if (location != null) {
            viewModel.getForecastByCoord(location.latitude, location.longitude)

        } else {
            viewModel.insertFavoriteCityId(FavoriteCityId(getDefaultCity().id))
        }
    }

    private fun setDefaultCity() {

        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener(onLocationSuccess)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {

                AlertDialog.Builder(activity!!)
                    .setTitle(R.string.title_location_permission)
                    .setMessage(R.string.text_location_permission)
                    .setPositiveButton(R.string.ok) { _, _ ->

                        requestPermissionsSafely(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ), MY_PERMISSIONS_REQUEST_LOCATION
                        )

                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    setDefaultCity()
                }

            } else {
                viewModel.insertFavoriteCityId(FavoriteCityId(getDefaultCity().id))
            }
        }
    }

    private fun getDefaultCity() = City(2643743, "London", "GB", Coordinates(51.50853, -0.12574))

    private fun setupRecyclerView() {
        mAdapter = AppRecyclerViewAdapter(this, object : DiffUtil.ItemCallback<City>() {
            override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem.id == newItem.id
                        && oldItem.name == newItem.name
                        && oldItem.country == newItem.country
            }

        })

        mAdapter!!.setItemViewType(R.layout.item_city)
        val recyclerView = mViewDataBinding!!.recyclerViewCities
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
        dismissServicesDialog()
        val intent = Intent(activity, WeatherDetailsActivity::class.java)
        intent.putExtra("city", city)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        } else {
            startActivity(intent)
        }
    }

    override fun onAddCityClicked(city: City) {
        if (isFavCitiesListCompleted!!) {
            Toast.makeText(context, R.string.fav_cities_list_completed, Toast.LENGTH_LONG).show()
            return
        }
        viewModel.insertFavoriteCityId(FavoriteCityId(city.id))
    }

    override fun onDeleteCityClicked(city: City) {
        viewModel.deleteFavoriteCityId(city)
    }

    fun showSearchDialog() {
        if (alertDialogBuilder == null) {
            alertDialogBuilder = AlertDialog.Builder(activity!!)
            dialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_cities_search,
                null,
                false
            )
            alertDialogBuilder!!.setView(dialogBinding!!.root)
            dialogBinding!!.view = this

            setupSearchRecyclerView()
            setSearchView()

            alertDialogCities = alertDialogBuilder!!.create()
            alertDialogCities!!.show()
            alertDialogCities!!.window!!.attributes.height = (ScreenUtils.getScreenHeight(activity!!) * 0.8).toInt()
            alertDialogCities!!.window!!.attributes.width = (ScreenUtils.getScreenWidth(activity!!) * 0.9).toInt()
            alertDialogCities!!.dismiss()
        }
        if (!alertDialogCities!!.isShowing) {
            alertDialogCities!!.show()
            viewModel.initSearchBox()
        }
    }

    private fun setSearchView() {
        val searchView = dialogBinding!!.searchView

        disposable = RxSearchObservable.fromView(searchView)
            .debounce(1000, TimeUnit.MILLISECONDS)
            .map { text -> text.toLowerCase().trim() }
            .distinctUntilChanged()
            .switchMap {
                Observable.just(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { query ->
                search(query)
            }
    }

    private fun search(query: String?) {
        viewModel.queryCityByName(query!!)
    }

    private fun setupSearchRecyclerView() {
        searchResultsAdapter = AppRecyclerViewAdapter(this, object : DiffUtil.ItemCallback<City>() {
            override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
                return oldItem.id == newItem.id
                        && oldItem.name == newItem.name
                        && oldItem.country == newItem.name
            }

        })

        searchResultsAdapter!!.setItemViewType(R.layout.item_searched_city)
        val recyclerView = dialogBinding!!.recyclerViewSearchResults
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = searchResultsAdapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun dismissServicesDialog() {
        if(alertDialogCities!= null){
            alertDialogCities!!.dismiss()
        }
    }

    override fun onDetach() {
        if (disposable != null) {
            disposable!!.dispose()
        }
        super.onDetach()
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION: Int = 1994
    }

}

