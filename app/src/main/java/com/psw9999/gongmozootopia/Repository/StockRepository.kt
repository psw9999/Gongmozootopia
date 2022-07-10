package com.psw9999.gongmozootopia.Repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.psw9999.gongmozootopia.communication.ServerImpl
import com.psw9999.gongmozootopia.data.StockListItem
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.paging.StockListPagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.await

class StockRepository {
    private val dbsgAPI = ServerImpl.APIService

    fun getStockDataByPaging() : Flow<PagingData<StockListItem>> {
        return Pager(PagingConfig(pageSize = 10)) {
            StockListPagingSource(dbsgAPI)
        }.flow
    }
}