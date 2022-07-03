package com.psw9999.gongmozootopia.communication

import com.psw9999.gongmozootopia.data.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface dbsgAPI {
    @GET("/login/kakaoAccessToken")
    fun getPost(
        @Query("accessToken") accessToken : String
    ) : retrofit2.Call<LoginData>

    @GET("/api/v1/ipo")
    fun getIpoList(
        @Query("queryString") queryString : String
    ) : retrofit2.Call<ArrayList<StockResponse>>

    @GET("/api/v1/ipo")
    fun getIpoList(
        @Query("page") page : Int,
        @Query("num") num : Int = 10,
        @Query("queryString") queryString : String
    ) : retrofit2.Call<ArrayList<StockResponse>>

    @GET("/api/v1/ipo/detail/{ipoIndex}")
    fun getStockInfo(@Path("ipoIndex") ipoIndex : Long) : retrofit2.Call<StockInfoResponse>

    @GET("/api/v1/ipo/underwriter/{ipoIndex}")
    fun getUnderwriter(@Path("ipoIndex") ipoIndex : Long) : retrofit2.Call<ArrayList<UnderwriterResponse>>

    @GET("/api/v1/ipo/schedule")
    fun getStockSchedule(@Query("startDate") startDate : String, @Query("endDate") endDate : String) : retrofit2.Call<ArrayList<ScheduleResponse>>

    @GET("/api/v1/ipo/comment")
    fun getComments() : retrofit2.Call<ArrayList<CommentResponse>>

}