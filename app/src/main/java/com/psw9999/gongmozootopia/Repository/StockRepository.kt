package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.communication.ServerImpl
import com.psw9999.gongmozootopia.data.StockResponse
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

                // 증권사 데이터 체크 및 수정
                data.underwriter?.let{ underwriter ->
                    val words = Regex("증권|투자|금융")
                    var sb = StringBuilder()
                    underwriter.split(",").forEach { temp ->
                        if (temp.contains(words)) {
                            sb.append(temp.replace("증권","").replace("투자","").replace("금융","").replace("㈜","").plus(","))
                        }
                    }
                    if (sb.isEmpty()) data.underwriter = null
                    else data.underwriter = sb.substring(0, sb.length-1)
                }
                stockData.add(data)
            }
        }
        return stockData
    }

}