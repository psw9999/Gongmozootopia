package com.psw9999.gongmozootopia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.configurationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ConfigurationViewModel : ViewModel() {

    val stockFirmMap : LiveData<Map<String, Boolean>>
        get() = configurationManager.stockFirmFlow.distinctUntilChanged().asLiveData()

    fun setStockFirmData(stockName : String, isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            configurationManager.setStockFirmEnable(stockName, isEnabled)
        }
    }

    val forfeitedFlow : Flow<Boolean>
        get() = configurationManager.forfeitedStockFlow

    fun setForfeitedEnabled(isEnabled : Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            configurationManager.setForfeitedEnabled(isEnabled)
        }
    }

    val spacFlow : Flow<Boolean>
        get() = configurationManager.spacStockFlow

    fun setSpacEnabled(isEnabled : Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            configurationManager.setSpacEnabled(isEnabled)
        }
    }

    val kindFilterFlow : Flow<Array<String>> =
        configurationManager.forfeitedStockFlow.combine(configurationManager.spacStockFlow) { forfeited, spac ->
            val filterArray = arrayOf("공모주", "실권주", "스팩주")
            if (!forfeited) filterArray[1] = ""
            if (!spac) filterArray[2] = ""
            filterArray
        }
}