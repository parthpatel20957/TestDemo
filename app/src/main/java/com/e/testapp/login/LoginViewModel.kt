package com.e.testapp.login

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.e.testapp.api.PostApi
import com.e.testapp.base.BaseViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoginViewModel : BaseViewModel() {
    @Inject
    lateinit var postApi: PostApi

    var userName = ObservableField("")
    var pasword = ObservableField("")
    var imsi = ""
    var imei = ""
    var isProgressVisible = ObservableField(false)

    fun callLoginApi(): MutableLiveData<Response<ResponseBody>> {
        isProgressVisible.set(true)
        val apiResponse = MutableLiveData<Response<ResponseBody>>()
        val jsonObject = JSONObject()
        jsonObject.put("username", userName.get().toString())
        jsonObject.put("password", pasword.get().toString())

        postApi.login("application/json", imsi, imei, jsonObject.toString())
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    isProgressVisible.set(false)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    isProgressVisible.set(false)
                    apiResponse.postValue(response)
                }

            })


        return apiResponse

    }


}