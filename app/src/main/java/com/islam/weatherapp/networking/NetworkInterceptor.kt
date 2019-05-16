package com.islam.weatherapp.networking

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkInterceptor(

    /*
     * pass the context instance here,
     * since we need to get the ConnectivityStatus
     * to check if there is internet.
     * */
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()

        /*
         * check if there is internet
         * available in the device. If not, pass
         * the networkState as NO_INTERNET.
         * */

        if (!ConnectivityStatus.isConnected(context)) {
            NetworkEvent.publish(NetworkState.NO_INTERNET)
            Log.e("okhttp", "NO INTERNET CONNECTION")
        } else {
            try {
                /*
                 * Get the response code from the
                 * request. In this scenario we are only handling
                 * unauthorised and server unavailable error
                 * scenario
                 * */
                val response = chain.proceed(request)
                when (response.code()) {
                    401 -> {
                        NetworkEvent.publish(NetworkState.UNAUTHORIZED)
                        Log.e("okhttp", "UNAUTHORIZED - CODE 401")
                    }

                    500, 501, 502, 503 -> {
                        NetworkEvent.publish(NetworkState.NO_RESPONSE)
                        Log.e("okhttp", "NO_RESPONSE - CODE ${response.code()} -> ${response.message()}")
                    }
                }
                return response

            } catch (e: IOException) {
                NetworkEvent.publish(NetworkState.NO_RESPONSE)
            }
        }
        return null
    }
}