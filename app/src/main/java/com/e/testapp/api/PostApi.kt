package com.e.testapp.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PostApi {

    @POST("login")
    fun login(
        @Header("Content-Type") content_type: String?,
        @Header("IMSI") imsi: String?,
        @Header("IMEI") imei: String?,
        @Body req: String?
    ): Call<ResponseBody>
}