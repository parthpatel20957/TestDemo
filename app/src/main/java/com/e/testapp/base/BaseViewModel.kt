package com.e.testapp.base

import androidx.lifecycle.ViewModel
import com.e.testapp.api.ApiClient
import com.e.testapp.login.LoginViewModel

abstract class BaseViewModel : ViewModel() {
    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .networkModule(ApiClient)
        .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is LoginViewModel -> injector.inject(this)
        }
    }
}
