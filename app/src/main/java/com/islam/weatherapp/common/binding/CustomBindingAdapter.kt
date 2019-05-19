package com.islam.weatherapp.common.binding

import android.view.View
import androidx.databinding.BindingAdapter


object CustomBindingAdapter {
    /* @BindingAdapter("image")
     @JvmStatic
     fun setImage(view: ImageView?, url: String) {
         if (view != null && !TextUtils.isEmpty(url)) {
             Picasso.with(view.context).load(url.trim())
                 .fit()
                 .placeholder(R.drawable.ic_no_image)
                 .error(R.drawable.ic_no_image)
                 .into(view)
         }
     }*/

    @BindingAdapter("height")
    @JvmStatic
    fun setHeight(view: View?, height: Double) {
        view?.let {
            view.layoutParams.height = height.toInt()
        }
    }

    @BindingAdapter("width")
    @JvmStatic
    fun setWidth(view: View?, width: Double?) {
        view?.let {
            view.layoutParams.width = width!!.toInt()
        }

    }
}