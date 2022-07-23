package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.communication.ServerImpl
import com.psw9999.gongmozootopia.data.UnderwriterResponse
import retrofit2.await

class UnderwriterRepository {
    private val dbsgAPI = ServerImpl.APIService

    suspend fun getUnderwriters(ipoIndex : Long) : ArrayList<UnderwriterResponse>  {
        val request = dbsgAPI.getUnderwriter(ipoIndex)
        return request.await()
    }
}