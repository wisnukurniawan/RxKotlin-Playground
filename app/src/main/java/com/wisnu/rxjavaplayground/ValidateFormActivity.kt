package com.wisnu.rxjavaplayground

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import com.jakewharton.rxbinding2.widget.afterTextChangeEvents
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_validate_form.*
import java.util.concurrent.TimeUnit

/**
 * Created by wisnu on 11/26/17.
 */
class ValidateFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_form)

        email_field.afterTextChangeEvents()
            .skipInitialValue()
            .map {
                email_wrapper.error = null
                it.editable().toString()
            }
            .debounce(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(lengthGreaterThanSix)
            .compose(verifyEmailPattern)
            .compose(retryWhenError {
                email_wrapper.error = it.message
            })
            .subscribe()


    }

    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> =
        ObservableTransformer { observable ->
            observable.retryWhen { errors ->
                errors.flatMap {
                    onError(it)
                    Observable.just("")
                }
            }
        }

    private val lengthGreaterThanSix =
        ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                Observable
                    .just(it)
                    .map { it.trim() }
                    .filter { it.length > 6 }
                    .singleOrError()
                    .onErrorResumeNext { Single.error(Exception("Length should be greater than 6")) }
                    .toObservable()
            }
        }

    private val verifyEmailPattern =
        ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                Observable
                    .just(it)
                    .map { it.trim() }
                    .filter { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
                    .singleOrError()
                    .onErrorResumeNext { Single.error(Exception("Email not valid")) }
                    .toObservable()
            }
        }

}