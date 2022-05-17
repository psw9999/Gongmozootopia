package com.psw9999.gongmozootopia.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScheduleViewModel : ViewModel() {
    // 0 : 수요예측일, 1 : 청약시작일, 2 : 청약마감일, 2 : 환불일, 3 : 상장일
    var scheduleFilteringList = MutableLiveData<Array<Boolean>>()

    init {
        scheduleFilteringList.value = arrayOf(true, true, true, true, true)
    }
}