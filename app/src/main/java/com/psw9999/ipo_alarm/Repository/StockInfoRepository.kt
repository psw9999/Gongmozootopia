package com.psw9999.ipo_alarm.Repository

import android.util.Log
import com.psw9999.ipo_alarm.communication.ServerImpl
import com.psw9999.ipo_alarm.data.StockListResponse
import com.psw9999.ipo_alarm.data.StockInfo
import kotlinx.coroutines.runBlocking
import retrofit2.await

class StockInfoRepository {
    private val dbsgAPI = ServerImpl.APIService
    private lateinit var stockInfo : StockInfo

    // TODO : run, catch문 사용하기
    fun getStockInfo(ipoIndex : Long) : StockInfo  {
        runBlocking {
            val request = dbsgAPI.getStockInfo(ipoIndex)
            val response = request.await()
            Log.d("getStockList",response.stockInfo.toString())
            stockInfo = response.stockInfo
        }
        return stockInfo
    }

}