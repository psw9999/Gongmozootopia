package com.psw9999.gongmozootopia.Repository

import android.util.Log
import com.psw9999.gongmozootopia.communication.ServerImpl
import com.psw9999.gongmozootopia.data.StockInfo
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