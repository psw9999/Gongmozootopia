package com.psw9999.gongmozootopia.ViewModel

import androidx.lifecycle.*
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.settingsManager

class StockFirmViewModel : ViewModel(){
    val stockFirmData : LiveData<Map<String,Boolean>>
        get() = settingsManager.stockFirmFlow.asLiveData()
}