package com.psw9999.gongmozootopia.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.psw9999.gongmozootopia.data.StockFollowingResponse
import com.psw9999.gongmozootopia.Repository.StockFollowingRepository
import com.psw9999.gongmozootopia.Room.StockDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ViewModel은 직접 DB에 접근하지 않도록 함.
class StockFollowingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : StockFollowingRepository

    val stockFollowingIndexData : LiveData<List<Long>>
        get() = repository.getAllFollowingStockLiveData

    init {
        val stockFollowingDAO = StockDatabase.getDatabase(application)!!.stockFollowingDAO()
        repository = StockFollowingRepository(stockFollowingDAO)
    }

    fun addStock(stockFollowingResponse: StockFollowingResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addStock(stockFollowingResponse)
        }
    }

    fun deleteStock(ipoIndex : Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStock(ipoIndex)
        }
    }

    fun updateStockFollowing(ipoIndex : Long, isFollowing : Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStockFollowing(ipoIndex = ipoIndex, isFollowing = isFollowing)
        }
    }

    fun getStockFollowing(ipoIndex : Long) : Boolean{
        var isFollowing : Boolean = false
        viewModelScope.launch(Dispatchers.IO) {
            isFollowing = repository.getStockFollowing(ipoIndex)
        }
        return isFollowing
    }

}