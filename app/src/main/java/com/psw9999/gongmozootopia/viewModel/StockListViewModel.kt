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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StockListViewModel(application: Application) : AndroidViewModel(application) {
    private val followingDAO = FollowingDatabase.getDatabase(application)!!.followingDAO()
    private val followingRepository = FollowingListRepository(followingDAO)
    private val stockListRepository  = StockRepository()
    val kindsFilteringArray = arrayOf("공모주","실권주","스팩주")

    val stockList
        = stockListRepository.getStockDataByPaging().cachedIn(viewModelScope)

    val followingList
        = followingRepository.followingListFlow.asLiveData()

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