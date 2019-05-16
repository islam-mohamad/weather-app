package com.islam.weatherapp.utils


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import android.util.Patterns
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout


object CommonUtils {


    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isMobileValid(mobile: String): Boolean {
        return mobile.startsWith("01") && mobile.length == 11
    }


    fun showProgressBar(context: Context, layout: ViewGroup): ProgressBar {
        val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(100, 100)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        layout.addView(progressBar, params)
        return progressBar
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }
}