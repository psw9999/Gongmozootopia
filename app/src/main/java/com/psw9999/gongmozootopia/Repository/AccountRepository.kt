package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.communication.ServerImpl
import com.psw9999.gongmozootopia.data.StockListResponse
import kotlinx.coroutines.runBlocking
import retrofit2.await
import java.time.LocalDate

class AccountRepository {
    private val dbsgAPI = ServerImpl.APIService
    private var stockList = arrayListOf<StockListResponse>()

    fun getStockList() : ArrayList<StockListResponse> {
        runBlocking {
            val request = dbsgAPI.getStockList()
            val response = request.await()
            for (stock in response) {
                if (stock.stockKinds == null) continue

                // 날짜 형식에 맞지 않은 데이터가 들어온 경우 수정해주기
                var index = 0
                try {
                    val timeDate = arrayOf(
                        stock.ipoStartDate,
                        stock.ipoEndDate,
                        stock.ipoDebutDate,
                        stock.ipoRefundDate
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
                        stock.ipoDebutDate = null
                        stock.ipoRefundDate = null
                    }
                    else {
                        stock.ipoDebutDate = null
                    }

                }
                stockList.add(stock)
            }
        }
        return stockList
    }

}