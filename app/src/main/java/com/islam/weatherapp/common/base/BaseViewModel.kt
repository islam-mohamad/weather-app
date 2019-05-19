package com.islam.weatherapp.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable


abstract class BaseViewModel : ViewModel() {


    private val mIsLoading = MutableLiveData<Boolean>()
    private val mErrorMessage = MutableLiveData<String>()
    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val isLoading: LiveData<Boolean>
        get() = mIsLoading

    val errorMessage: LiveData<String>
        get() = mErrorMessage

    init {
        this.mIsLoading.value = false
        this.mErrorMessage.value = ""
    }

    fun setIsLoading(isLoading: Boolean) {
        mIsLoading.value = isLoading
    }

    fun setErrorMessage(errorMessage: String?) {
        mErrorMessage.value = errorMessage
    }

    override fun onCleared() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        super.onCleared()
    }
}