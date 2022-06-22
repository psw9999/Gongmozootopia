package com.psw9999.gongmozootopia.viewModel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.psw9999.gongmozootopia.Repository.FollowingListRepository
import com.psw9999.gongmozootopia.Repository.StockRepository
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.Room.FollowingDatabase
import com.psw9999.gongmozootopia.data.StockResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockListViewModel(application: Application) : AndroidViewModel(application) {
    private val followingRepository : FollowingListRepository
    private val stockListRepository  = StockRepository()

    val kindsFilteringArray = arrayOf("공모주","실권주","스팩주")

    val _stockList
        = stockListRepository.getStockDataByPaging().cachedIn(viewModelScope)
    val followingList : LiveData<List<Long>>

    init {
        val followingDAO = FollowingDatabase.getDatabase(application)!!.followingDAO()
        followingRepository = FollowingListRepository(followingDAO)
        followingList = followingRepository.followingListFlow.asLiveData()
    }

    fun addFollowing(followingResponse: FollowingResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            followingRepository.addFollowing(followingResponse)
        }
    }

    fun deleteFollowing(ipoIndex : Long) {
        viewModelScope.launch(Dispatchers.IO) {
            followingRepository.deleteFollowing(ipoIndex)
        }
    }

//    fun followingListFiltering() = _stockList.value!!.filter { stock ->
//        stock.stockKinds in kindsFilteringArray
//    }
}