package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.data.StockScheduleResponse
import com.psw9999.gongmozootopia.communication.ServerImpl
import kotlinx.coroutines.runBlocking
import retrofit2.await

class StockScheduleRepository {
    private val dbsgAPI = ServerImpl.APIService
    private var scheduleData = arrayListOf<StockScheduleResponse>()
    private val ERROR_DAY = "0000-00-00"

    fun getScheduleData(startDate : String, endDate : String) : ArrayList<StockScheduleResponse> {
        runBlocking {
            val request = dbsgAPI.getStockSchedule(startDate, endDate)
            val response = request.await()
            for (rsp in response) {
                if (rsp.ipoForecastDate == ERROR_DAY) rsp.ipoForecastDate = null
                if (rsp.ipoStartDate == ERROR_DAY) rsp.ipoStartDate = null
                if (rsp.ipoEndDate == ERROR_DAY) rsp.ipoEndDate = null
                if (rsp.ipoRefundDate == ERROR_DAY) rsp.ipoRefundDate = null
                if (rsp.ipoDebutDate == ERROR_DAY) rsp.ipoDebutDate = null
            }
            scheduleData = response
        }
        return scheduleData
    }
}