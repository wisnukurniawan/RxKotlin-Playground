package com.wisnu.rxjavaplayground

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jakewharton.rxbinding2.widget.afterTextChangeEvents
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

/**
 * Created by wisnu on 11/26/17.
 */
class SearchActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        var apiCallCount = 1
        search_field.afterTextChangeEvents()
            .debounce(1, TimeUnit.SECONDS)
            .map { it.editable().toString() }
            .doOnNext {
                Log.d("TAG - ", "$it")
            }
            .filter { it.isNotBlank() }
            .switchMap {
                // call api here
                Observable.just("Jln. $it")
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                search_info.text = """
                    |API CALL ${apiCallCount++} TIMES
                    |
                    |Result: $it
                """.trimMargin()
            }
    }

}