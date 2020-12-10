package com.e.testapp.base

import com.e.testapp.api.ApiClient
import com.e.testapp.login.LoginViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Component providing inject() methods for presenters.
 */
@Singleton
@Component(modules = [(ApiClient::class)])
interface ViewModelInjector {

    fun inject(loginViewModel: LoginViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun networkModule(networkModule: ApiClient): Builder
    }
}