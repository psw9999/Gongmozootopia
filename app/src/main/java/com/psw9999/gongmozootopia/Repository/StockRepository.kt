package com.psw9999.gongmozootopia.Repository

import android.util.Log
import com.psw9999.gongmozootopia.communication.ServerImpl
import com.psw9999.gongmozootopia.Data.StockResponse
import kotlinx.coroutines.runBlocking
import retrofit2.await
import java.time.LocalDate

class StockRepository {
    private val dbsgAPI = ServerImpl.APIService
    private var stockData = arrayListOf<StockResponse>()

    fun getStockData() : ArrayList<StockResponse> {
        runBlocking {
            val request = dbsgAPI.getStockList()
            val response = request.await()
            for (data in response) {
                if (data.stockKinds == null || data.ipoIndex == null) continue

                // 날짜 형식에 맞지 않은 데이터가 들어온 경우 수정해주기
                var index = 0
                try {
                    val timeDate = arrayOf(
                        data.ipoStartDate,
                        data.ipoEndDate,
                        data.ipoDebutDate,
                        data.ipoRefundDate
                    )
                    for (i in timeDate.indices) {
                        LocalDate.parse(timeDate[i])
                        index++
                    }
                }catch(e : Exception){
                    if (index == 0 || index == 1) {
                        continue
                    }
                    else if (index == 2) {
                        data.ipoDebutDate = null
                        data.ipoRefundDate = null
                    }
                    else {
                        data.ipoDebutDate = null
                    }
                }
                stockData.add(data)
            }
        }
        return stockData
    }

}