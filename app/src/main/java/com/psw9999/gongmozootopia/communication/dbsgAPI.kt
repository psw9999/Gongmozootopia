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
    fun getStockList(
        @Query("queryString") queryString : String =
                        "(ipo_start_date BETWEEN '2022-06-30' AND '2022-06-30') OR" +
                        "(ipo_end_date BETWEEN '2022-06-30' AND '2022-06-30') OR" +
                        "(ipo_refund_date BETWEEN '2022-06-30' AND '2022-06-30') OR" +
                        "(ipo_debut_date BETWEEN '2022-06-30' AND '2022-06-30') and stock_exchange is not null and stock_kinds is not null and ipo_start_date is not null"
    ) : retrofit2.Call<ArrayList<StockResponse>>

    @GET("/api/v1/ipo")
    fun getStockList(
        @Query("page") page : Int,
        @Query("num") num : Int = 10,
        @Query("queryString") queryString : String = "stock_exchange is not null and stock_kinds is not null and ipo_start_date is not null"
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