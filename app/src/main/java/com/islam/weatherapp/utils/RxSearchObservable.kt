package com.islam.weatherapp.utils

import androidx.appcompat.widget.SearchView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


object RxSearchObservable {
    fun fromView(searchView: SearchView): Observable<String> {

        val subject = PublishSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                subject.onNext(newText!!)
                return true

            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                subject.onComplete()
                return true
            }

        })

        return subject
    }
}
