package com.psw9999.ipo_alarm.communication

import com.psw9999.ipo_alarm.data.LoginData
import com.psw9999.ipo_alarm.data.StockListResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitService {

    @GET("/login/kakaoAccessToken")
    fun getPost(
        @Query("accessToken") accessToken : String
    ) : retrofit2.Call<LoginData>

    @GET("/api/v1/ipo")
    fun getStockList() : retrofit2.Call<ArrayList<StockListResponse>>
}