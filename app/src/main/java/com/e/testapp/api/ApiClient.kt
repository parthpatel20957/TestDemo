package com.e.testapp.api

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


const val BASE_URL = "http://private-222d3-homework5.apiary-mock.com/api/"

/**
 * Module which provides all required dependencies about network
 */
@Module
// Safe here as we are dealing with a Dagger 2 module
@Suppress("unused")
object ApiClient {
    /**
     * Provides the Post service implementation.
     * @param retrofit the Retrofit object used to instantiate the service
     * @return the Post service implementation.
     */
    @Provides
    @Reusable
    @JvmStatic
    internal fun providePostApi(retrofit: Retrofit): PostApi {
        return retrofit.create(PostApi::class.java)
    }

    /**
     * Provides the Retrofit object.
     * @return the Retrofit object
     */
    @Provides
    @Reusable
    @JvmStatic
    internal fun provideRetrofitInterface(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

}

interface SingleCallback<T> {
    /**
     * @param o        Whole response Object
     * @param apiNames [A] to differentiate Apis
     */
    fun onSingleSuccess(o: T)

    /**
     * @param throwable returns [Throwable] for checking Exception
     * @param apiNames  [A] to differentiate Apis
     */
    fun onFailure(throwable: Throwable)
}

fun <T> subscribeToSingle(observable: Observable<T>, singleCallback: SingleCallback<T>?) {
    Single.fromObservable(observable)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(object : SingleObserver<T> {
            override fun onSuccess(t: T) {
                try {
                    singleCallback?.onSingleSuccess(t)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                when (e) {
                    is HttpException -> {
                        if (e.code() == 401) {
                            //singleCallback?.onFailure(e)
                        } else if (e.code() == 500) {
                            singleCallback?.onFailure(e)
                        } else {
                        }
                    }
                    else -> singleCallback?.onFailure(e)
                }
            }
        })
}