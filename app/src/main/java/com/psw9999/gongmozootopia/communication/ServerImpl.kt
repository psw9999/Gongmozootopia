package com.psw9999.gongmozootopia.communication

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerImpl {

    val interceptor: AuthInterceptor = AuthInterceptor()
    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    private const val BASE_URL = "http://server.dbsg.co.kr:8080/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val APIService : dbsgAPI = retrofit.create(dbsgAPI::class.java)
}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var req =
            //chain.request().newBuilder().addHeader("Authorization", "Bearer ${BaseApplication.preferences.JWS}").build()
            chain.request().newBuilder().addHeader("Authorization", "Bearer ").build()
        return chain.proceed(req)
    }
}