package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.Data.StockInfoResponse
import com.psw9999.gongmozootopia.Data.UnderwriterResponse
import com.psw9999.gongmozootopia.communication.ServerImpl
import kotlinx.coroutines.runBlocking
import retrofit2.await

class UnderwriterRepository {
    private val dbsgAPI = ServerImpl.APIService
    private lateinit var underwriters : ArrayList<UnderwriterResponse>

    fun getUnderwriters(ipoIndex : Long) : ArrayList<UnderwriterResponse>  {
        runBlocking {
            val request = dbsgAPI.getUnderwriter(ipoIndex)
            var response = request.await()
            underwriters = response
        }
        return underwriters
    }
}