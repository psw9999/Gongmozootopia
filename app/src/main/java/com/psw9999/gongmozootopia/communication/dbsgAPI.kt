package com.psw9999.gongmozootopia.communication

import com.psw9999.gongmozootopia.data.LoginData
import com.psw9999.gongmozootopia.data.StockListResponse
import com.psw9999.gongmozootopia.data.StockInfoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface dbsgAPI {

    @GET("/login/kakaoAccessToken")
    fun getPost(
        @Query("accessToken") accessToken : String
    ) : retrofit2.Call<LoginData>

    @GET("/api/v1/ipo")
    fun getStockList() : retrofit2.Call<ArrayList<StockListResponse>>

    @GET("/api/v1/ipo/{ipoIndex}")
    fun getStockInfo(@Path("ipoIndex") ipoIndex : Long) : retrofit2.Call<StockInfoResponse>
}