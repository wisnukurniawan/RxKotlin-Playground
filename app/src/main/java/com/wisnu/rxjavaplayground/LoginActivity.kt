package com.wisnu.rxjavaplayground

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by wisnu on 12/15/17.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initLogin()
        initEnableLoginButton()
        initEmailChangeEvents()
        initPasswordChangeEvents()
    }

    private fun initLogin() {
        login_btn.clicks()
            .observeOn(Schedulers.io())
            .flatMap {
                Observable.just(true)  // Call API misalnya
            }
            .filter { it }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    // do on next
                    launchHomeActivity()
                },
                {
                    // do on error
                },
                {
                    // do on complete
                }
            )
    }

    private fun launchHomeActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initEnableLoginButton() {
        Observable.just(true)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                Observable.combineLatest(
                    emailValidationObservable(),
                    passwordValidationObservable(),
                    BiFunction<Boolean, Boolean, Boolean> { emailValid, passwordValid ->
                        emailValid && passwordValid
                    }
                )
            }
            .subscribe { login_btn.isEnabled = it }
    }

    private fun emailValidationObservable(): Observable<Boolean> {
        return email_edt.textChanges()
            .map { it.toString() }
            .map { !it.isEmpty() && isValidEmail(it) }
            .distinctUntilChanged()
    }

    private fun passwordValidationObservable(): Observable<Boolean> {
        return password_edt.textChanges()
            .map { it.toString() }
            .map { !it.isEmpty() && isValidPassword(it) }
            .distinctUntilChanged()
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length > 6
    }

    private fun initEmailChangeEvents() {
        email_edt.textChanges()
            .skipInitialValue()
            .map { it.toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { email_container.error = null }
            .subscribe {
                if (!isValidEmail(it)) {
                    email_container.isErrorEnabled = true
                    email_container.error = "Email tidak valid"
                } else {
                    email_container.error = null
                    email_container.isErrorEnabled = false
                }
            }
    }

    private fun initPasswordChangeEvents() {
        password_edt.textChanges()
            .skipInitialValue()
            .map { it.toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { password_container.error = null }
            .subscribe {
                if (!isValidPassword(it)) {
                    password_container.isErrorEnabled = true
                    password_container.error = "Password harus lebih dari 6 karakter"
                } else {
                    password_container.error = null
                    password_container.isErrorEnabled = false
                }
            }

    }

}