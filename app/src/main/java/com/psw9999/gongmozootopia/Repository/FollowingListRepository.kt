package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.DAO.FollowingDAO
import com.psw9999.gongmozootopia.data.FollowingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class FollowingListRepository(private val followingDAO : FollowingDAO) {

    val followingListFlow : Flow<List<Long>>
        get() = followingDAO.getFollowingListFlow().flowOn(Dispatchers.IO)

    fun addFollowing(followingResponse: FollowingResponse) {
        followingDAO.addStock(followingResponse)
    }

    fun deleteFollowing(ipoIndex: Long) {
        followingDAO.deleteStock(ipoIndex)
    }
}