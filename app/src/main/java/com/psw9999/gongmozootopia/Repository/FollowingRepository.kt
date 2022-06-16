package com.psw9999.gongmozootopia.Repository

import androidx.lifecycle.LiveData
import com.psw9999.gongmozootopia.DAO.FollowingDAO
import com.psw9999.gongmozootopia.data.FollowingResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class FollowingRepository(private val followingDAO : FollowingDAO, private val ipoIndex : Long) {

    val followingFlow : Flow<Boolean>
        get() = followingDAO.getFollowingFlow(ipoIndex)

    fun addFollowing(followingResponse: FollowingResponse) {
        followingDAO.addStock(followingResponse)
    }

    fun deleteFollowing(ipoIndex: Long) {
        followingDAO.deleteStock(ipoIndex)
    }

}