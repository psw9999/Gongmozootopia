package com.psw9999.gongmozootopia.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.psw9999.gongmozootopia.Repository.FollowingListRepository
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.Room.FollowingDatabase
import com.psw9999.gongmozootopia.data.StockResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : FollowingListRepository

    val kindsFilteringArray = arrayOf("공모주","실권주","스팩주")

    // 추후 Paging3 구현을 위해 MutableLiveData로 구현.
    val _stockList : MutableLiveData<ArrayList<StockResponse>> = MutableLiveData()
    val stockList : List<StockResponse>
        get() =followingListFiltering()

    val followingList : LiveData<List<Long>>

    init {
        val followingDAO = FollowingDatabase.getDatabase(application)!!.followingDAO()
        repository = FollowingListRepository(followingDAO)
        followingList = repository.followingListFlow.asLiveData()
    }

    fun addFollowing(followingResponse: FollowingResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFollowing(followingResponse)
        }
    }

    fun deleteFollowing(ipoIndex : Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFollowing(ipoIndex)
        }
    }

    fun followingListFiltering() = _stockList.value!!.filter { stock ->
        stock.stockKinds in kindsFilteringArray
    }
}