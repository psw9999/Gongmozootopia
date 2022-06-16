package com.psw9999.gongmozootopia.Repository

import com.psw9999.gongmozootopia.DAO.FollowingDAO
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockResponse
import kotlinx.coroutines.flow.Flow

class FollowingListRepository(private val followingDAO : FollowingDAO) {

    val followingListFlow : Flow<List<Long>>
        get() = followingDAO.getFollowingListFlow()

    fun addFollowing(followingResponse: FollowingResponse) {
        followingDAO.addStock(followingResponse)
    }

    fun deleteFollowing(ipoIndex: Long) {
        followingDAO.deleteStock(ipoIndex)
    }
}