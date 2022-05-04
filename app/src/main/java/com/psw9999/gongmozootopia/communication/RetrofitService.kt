package com.psw9999.gongmozootopia.communication

import com.psw9999.gongmozootopia.data.LoginData
import com.psw9999.gongmozootopia.data.StockResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitService {

    @GET("/login/kakaoAccessToken")
    fun getPost(
        @Query("accessToken") accessToken : String
    ) : retrofit2.Call<LoginData>

    @GET("/api/v1/ipo")
    fun getStockList() : retrofit2.Call<ArrayList<StockResponse>>
}