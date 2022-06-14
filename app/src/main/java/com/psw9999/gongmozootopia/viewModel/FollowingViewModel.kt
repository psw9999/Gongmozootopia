package com.psw9999.gongmozootopia.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.Repository.FollowingRepository
import com.psw9999.gongmozootopia.Room.StockDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FollowingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository : FollowingRepository

    init {
        val stockFollowingDAO = StockDatabase.getDatabase(application)!!.stockFollowingDAO()
        repository = FollowingRepository(stockFollowingDAO, 10L)
    }

    fun addStock(followingResponse: FollowingResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFollowing(followingResponse)
        }
    }

    fun deleteStock(ipoIndex : Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFollowing(ipoIndex)
        }
    }

    fun updateStockFollowing(ipoIndex : Long, isFollowing : Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStockFollowing(ipoIndex = ipoIndex, isFollowing = isFollowing)
        }
    }

}