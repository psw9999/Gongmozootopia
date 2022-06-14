package com.psw9999.gongmozootopia.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.psw9999.gongmozootopia.Repository.FollowingRepository
import com.psw9999.gongmozootopia.Room.StockDatabase
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockInfoResponse
import com.psw9999.gongmozootopia.data.UnderwriterResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class StockInfoViewModel(application : Application, private val ipoIndex : Long) : AndroidViewModel(application) {

    private val repository : FollowingRepository
    lateinit var stockInfo : StockInfoResponse
    lateinit var underwriterInfo : ArrayList<UnderwriterResponse>
    private val _isFollowing : MutableLiveData<Boolean> = MutableLiveData()
    var isFollowing : LiveData<Boolean>

        init {
            val stockFollowingDAO = StockDatabase.getDatabase(application)!!.stockFollowingDAO()
            repository = FollowingRepository(stockFollowingDAO, ipoIndex)
            isFollowing = repository.followingFlow.asLiveData()
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
    }


class StockInfoViewModelFactory(private val application: Application, private val ipoIndex: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockInfoViewModel(application, ipoIndex) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}