package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.communication.ServerImpl
import com.psw9999.gongmozootopia.data.CommentResponse
import retrofit2.await

class CommentRepository {
    private val dbsgAPI = ServerImpl.APIService

    suspend fun getComments(): ArrayList<CommentResponse> {
        val request = dbsgAPI.getComments()
        return request.await()
    }
}