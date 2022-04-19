package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.Data.StockScheduleResponse
import com.psw9999.gongmozootopia.communication.ServerImpl
import kotlinx.coroutines.runBlocking
import retrofit2.await

class StockScheduleRepository {
    private val dbsgAPI = ServerImpl.APIService
    private var scheduleData = arrayListOf<StockScheduleResponse>()

    fun getScheduleData(startDate : String, endDate : String) : ArrayList<StockScheduleResponse> {
        runBlocking {
            val request = dbsgAPI.getStockSchedule(startDate, endDate)
            val response = request.await()
            scheduleData = response
        }
        return scheduleData
    }
}