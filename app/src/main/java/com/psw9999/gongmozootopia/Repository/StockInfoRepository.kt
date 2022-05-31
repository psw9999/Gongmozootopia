package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.data.*
import com.psw9999.gongmozootopia.communication.ServerImpl
import retrofit2.await

class StockInfoRepository {
    private val dbsgAPI = ServerImpl.APIService

    suspend fun getStockInfo(ipoIndex: Long): StockInfoResponse {
        val request = dbsgAPI.getStockInfo(ipoIndex)
        return request.await()
    }
}