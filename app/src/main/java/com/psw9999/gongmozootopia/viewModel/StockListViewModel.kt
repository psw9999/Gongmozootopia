package com.psw9999.gongmozootopia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.psw9999.gongmozootopia.Repository.FollowingListRepository
import com.psw9999.gongmozootopia.Repository.StockRepository
import com.psw9999.gongmozootopia.Room.FollowingDatabase
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StockListViewModel(application: Application) : AndroidViewModel(application) {
    private val followingDAO = FollowingDatabase.getDatabase(application)!!.followingDAO()
    private val followingRepository = FollowingListRepository(followingDAO)
    private val stockListRepository  = StockRepository()

    val stockList : Flow<PagingData<StockListItem>>

    val followingList
            = followingRepository.followingListFlow.asLiveData()

    init {
        stockList = searchStockList().cachedIn(viewModelScope)
    }

    private fun searchStockList() : Flow<PagingData<StockListItem>> =
        stockListRepository.getStockDataByPaging()

    fun addFollowing(followingResponse: FollowingResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            followingRepository.addFollowing(followingResponse)
        }
    }

    fun deleteFollowing(ipoIndex: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            followingRepository.deleteFollowing(ipoIndex)
        }
    }
}