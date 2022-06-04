package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.communication.ServerImpl
import com.psw9999.gongmozootopia.data.StockResponse
import kotlinx.coroutines.runBlocking
import retrofit2.await
import java.time.LocalDate

class StockRepository {
    private val dbsgAPI = ServerImpl.APIService

    suspend fun getStockData() : ArrayList<StockResponse> {
        val request = dbsgAPI.getStockList()
        return request.await()
    }

}