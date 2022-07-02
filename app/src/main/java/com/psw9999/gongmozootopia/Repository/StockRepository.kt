package com.psw9999.gongmozootopia.Repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.psw9999.gongmozootopia.DAO.FollowingDAO
import com.psw9999.gongmozootopia.communication.ServerImpl
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.paging.StockListPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import retrofit2.await
import java.time.LocalDate

class StockRepository {
    private val dbsgAPI = ServerImpl.APIService

    suspend fun getStockData() : ArrayList<StockResponse> {
        val request = dbsgAPI.getStockList()
        return request.await()
    }

    fun getStockDataByPaging() : Flow<PagingData<StockResponse>> {
        return Pager(PagingConfig(pageSize = 10)) {
            StockListPagingSource(dbsgAPI)
        }.flow
    }

    suspend fun getTodayStockData() : ArrayList<StockResponse> {
        val request = dbsgAPI.getStockList()
        return request.await()
    }
}