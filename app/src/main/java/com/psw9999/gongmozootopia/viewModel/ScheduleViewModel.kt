package com.psw9999.gongmozootopia.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.joda.time.DateTime

class ScheduleViewModel : ViewModel() {
    // 0 : 수요예측일, 1 : 청약시작일, 2 : 청약마감일, 2 : 환불일, 3 : 상장일
    //var scheduleFilteringList = MutableLiveData<MutableList<Boolean>>()
    var isIpoDayEnabled = MutableLiveData<Boolean>()
    var isRefundDayEnabled = MutableLiveData<Boolean>()
    var isDebutDayEnabled = MutableLiveData<Boolean>()
    var currentScheduleMoth = MutableLiveData<String>()
    var selectedDay = MutableLiveData<String>()

    init {
        isIpoDayEnabled.value = true
        isRefundDayEnabled.value = true
        isDebutDayEnabled.value = true
        currentScheduleMoth.value = DateTime().toString("yyyy년 MM월")
        selectedDay.value = DateTime().toString("yyyy년 MM월 dd일")
    }
}