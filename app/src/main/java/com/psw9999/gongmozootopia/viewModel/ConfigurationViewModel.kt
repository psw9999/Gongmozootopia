package com.psw9999.gongmozootopia.viewModel

import androidx.lifecycle.*
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.settingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfigurationViewModel : ViewModel(){
    val stockFirmData : LiveData<Map<String,Boolean>>
        get() = settingsManager.stockFirmFlow.asLiveData()

    fun setStockFirmData(stockName : String, isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsManager.setStockFirmEnable(stockName, isEnabled)
        }
    }

    val isForfeitedEnabled : LiveData<Boolean>
        get() = settingsManager.forfeitedStockFlow.asLiveData()

    fun setForfeitedEnabled(isEnabled : Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsManager.setForfeitedEnabled(isEnabled)
        }
    }

    val isSpacEnabled : LiveData<Boolean>
        get() = settingsManager.spacStockFlow.asLiveData()

    fun setSpacEnabled(isEnabled : Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsManager.setSpacEnabled(isEnabled)
        }
    }

}