package com.example.gigafood.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST
    fun postJson(
        @Url url: String,
        @Body body: Any,
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

    @GET
    fun getJson(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

    @Multipart
    @POST
    fun uploadFile(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>

    @POST
    fun downloadFile(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Call<ResponseBody>
}