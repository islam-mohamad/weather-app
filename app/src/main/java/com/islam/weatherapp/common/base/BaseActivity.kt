package com.islam.weatherapp.common.base

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import com.islam.weatherapp.R
import com.islam.weatherapp.model.remote.networking.NetworkEvent
import com.islam.weatherapp.model.remote.networking.NetworkState
import com.islam.weatherapp.common.utils.CommonUtils
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.functions.Consumer
import javax.inject.Inject


abstract class BaseActivity<T : ViewDataBinding> : DaggerAppCompatActivity(), HasActivityInjector,
    BaseFragment.Callback {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector


    private var mProgressBar: ProgressBar? = null
    private var mViewDataBinding: T? = null

    /**
     * @return layout resource id
     */
    @LayoutRes
    public abstract fun getLayoutId(): Int

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        mProgressBar = CommonUtils.showProgressBar(this, getViewDataBinding().root as ViewGroup)
    }

    override fun onResume() {
        super.onResume()
        NetworkEvent.register(this, Consumer {
            when (it!!) {
                NetworkState.NO_INTERNET -> displayErrorDialog(
                    getString(R.string.no_internet_title),
                    getString(R.string.no_internet_desc)
                )

                NetworkState.NO_RESPONSE -> displayErrorDialog(
                    getString(R.string.http_error_title),
                    getString(R.string.http_error_desc)
                )

                NetworkState.UNAUTHORIZED -> {
                    //redirect to login screen - if session expired
                    Toast.makeText(applicationContext, R.string.error_login_expired, Toast.LENGTH_LONG).show()
                    openActivityOnTokenExpire()
                }
            }
        })
    }

    /*
   * unregister the activity once it is finished.
   */
    override fun onStop() {
        super.onStop()
        NetworkEvent.unregister(this)
    }

    fun displayErrorDialog(
        title: String,
        desc: String
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(desc)
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.ok)
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    fun getViewDataBinding() = mViewDataBinding!!

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String) = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    fun hideKeyboard() {
        val view: View = this.currentFocus!!
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isNetworkConnected() = CommonUtils.isNetworkAvailable(applicationContext)

    fun openActivityOnTokenExpire() {
//        val intent = Intent(this, LgoinActivity::class)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//        finishAffinity()
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    fun showLoading() {
        mProgressBar!!.visibility = View.VISIBLE
    }

    fun hideLoading() {
        mProgressBar!!.visibility = View.GONE
    }

    fun showSnakeBar(message: String) {
        val snackBar = Snackbar.make(getViewDataBinding().root, message, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.WHITE)
        val sbView = snackBar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackBar.show()
    }

    private fun performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mViewDataBinding!!.executePendingBindings()
    }
}