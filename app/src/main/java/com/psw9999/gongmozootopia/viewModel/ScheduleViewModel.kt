package com.psw9999.gongmozootopia.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.today
import org.joda.time.DateTime

class ScheduleViewModel : ViewModel() {
    // 0 : 수요예측일, 1 : 청약시작일, 2 : 청약마감일, 2 : 환불일, 3 : 상장일
    var scheduleFilteringList = MutableLiveData<MutableList<Boolean>>()
    var currentScheduleMoth = MutableLiveData<String>()

    init {
        scheduleFilteringList.value = mutableListOf(true, true, true, true, true)
        currentScheduleMoth.value = DateTime(today).toString("yyyy년 MM월")
    }
}