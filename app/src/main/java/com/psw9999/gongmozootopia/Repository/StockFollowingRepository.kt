package com.psw9999.gongmozootopia.Repository

import androidx.lifecycle.LiveData
import com.psw9999.gongmozootopia.DAO.StockFollowingDAO
import com.psw9999.gongmozootopia.Data.StockFollowingResponse

class StockFollowingRepository(private val stockFollowingDAO : StockFollowingDAO) {

    val getAllFollowingStockLiveData : LiveData<List<Long>> = stockFollowingDAO.getAllFollowingLiveData()

    fun addStock(stockFollowingResponse: StockFollowingResponse) {
        stockFollowingDAO.addStock(stockFollowingResponse)
    }

    fun deleteStock(ipoIndex: Long) {
        stockFollowingDAO.deleteStock(ipoIndex)
    }

    fun updateStockFollowing(ipoIndex : Long, isFollowing : Boolean) {
        stockFollowingDAO.updateStockFollowing(ipoIndex = ipoIndex, isFollowing = isFollowing)
    }

    suspend fun getStockFollowing(ipoIndex: Long) : Boolean {
        return stockFollowingDAO.getStockFollowing(ipoIndex)
    }

}