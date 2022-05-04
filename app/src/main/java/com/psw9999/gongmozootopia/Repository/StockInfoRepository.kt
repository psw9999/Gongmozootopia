package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.data.*
import com.psw9999.gongmozootopia.communication.ServerImpl
import kotlinx.coroutines.runBlocking
import retrofit2.await

class StockInfoRepository {
    private val dbsgAPI = ServerImpl.APIService
    private lateinit var stockInfo : StockInfoResponse

    fun getStockInfo(ipoIndex : Long) : StockInfoResponse  {
        runBlocking {
            val request = dbsgAPI.getStockInfo(ipoIndex)
            var response = request.await()
            with(response) {
                if (ipoForecastDate == null) ipoForecastDate = "미정"
                if (ipoStartDate == null) ipoStartDate = "미정"
                if (ipoEndDate == null) ipoEndDate = "미정"
                if (ipoRefundDate == null) ipoRefundDate = "미정"
                if (ipoDebutDate == null) ipoDebutDate = "미정"
                if (sector == null) sector = "확인필요"
            }
            stockInfo = response
        }
        return stockInfo
    }

}