package com.psw9999.gongmozootopia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.psw9999.gongmozootopia.Repository.FollowingListRepository
import com.psw9999.gongmozootopia.Repository.StockRepository
import com.psw9999.gongmozootopia.Room.FollowingDatabase
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.fmt
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.today
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockListItem
import com.psw9999.gongmozootopia.data.StockResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

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