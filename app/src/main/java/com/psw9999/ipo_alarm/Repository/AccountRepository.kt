package com.psw9999.ipo_alarm.Repository

import android.util.Log
import com.psw9999.ipo_alarm.communication.ServerImpl
import com.psw9999.ipo_alarm.data.StockListResponse
import kotlinx.coroutines.runBlocking
import retrofit2.await

class AccountRepository {
    private val dbsgAPI = ServerImpl.APIService
    private lateinit var stockList : ArrayList<StockListResponse>

    // TODO : run, catch문 사용하기
    fun getStockList() : ArrayList<StockListResponse> {
        runBlocking {
            val request = dbsgAPI.getStockList()
            val response = request.await()
            Log.d("getStockList",response.toString())
            stockList = response
        }
        return stockList
    }


}