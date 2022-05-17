package com.psw9999.gongmozootopia.Adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.psw9999.gongmozootopia.CustomView.CalendarLabelView
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Repository.StockScheduleRepository
import com.psw9999.gongmozootopia.Util.CalendarUtils
import com.psw9999.gongmozootopia.ViewModel.ScheduleViewModel
import com.psw9999.gongmozootopia.data.ScheduleResponse
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.databinding.HolderCalendarBinding
import kotlinx.coroutines.*
import org.joda.time.DateTime
import java.util.ArrayList

class CalendarAdapter(fm : Fragment) : FragmentStateAdapter(fm){

    private var start: Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis

    override fun createFragment(position: Int): Fragment {
        val millis = getItemId(position)
        return CalendarFragment.newInstance(millis)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun getItemId(position: Int): Long = DateTime(start).plusMonths(position - START_POSITION).millis

    override fun containsItem(itemId: Long): Boolean {
        val date = DateTime(itemId)
        return date.dayOfMonth == 1 && date.millisOfDay == 0
    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }
}

class CalendarFragment : Fragment() {
    private var millis : Long = 0L
    private val scheduleArray = arrayOf<MutableList<Pair<Int,ScheduleResponse>>>()
    private lateinit var rows: List<GridLayout>
    private lateinit var monthList : List<DateTime>
    private val scheduleViewModel : ScheduleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            millis = it.getLong(MILLIS)
            monthList = CalendarUtils.getMonthList(DateTime(millis))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = HolderCalendarBinding.inflate(inflater,container,false)
        rows = listOf(
            viewBinding.GridLayoutWeek1,
            viewBinding.GridLayoutWeek2,
            viewBinding.GridLayoutWeek3,
            viewBinding.GridLayoutWeek4,
            viewBinding.GridLayoutWeek5,
            viewBinding.GridLayoutWeek6
        )

        CoroutineScope(Dispatchers.Main).launch {
            // 1. 캘린더의 첫날부터 마지막날까지의 일정 조회
            val deferredScheduleResponse = async(Dispatchers.IO) {
                StockScheduleRepository().getScheduleData(
                    monthList[0].toString("yyyy-MM-dd"),
                    monthList.last().toString("yyyy-MM-dd")
                )
            }

            // 2. 캘린더 일정 분류
            val updateScheduleJob = async(Dispatchers.Default) {
                val scheduleResponse = deferredScheduleResponse.await()
                updateSchedule(scheduleResponse)
                scheduleArray.forEach { it ->
                    it.sortBy { it.first }
                }
                true
            }

            // 3. Bind 함수 실행
            launch {
                var temp = updateScheduleJob.await()
                onBindDay()
            }
        }

        return viewBinding.root
    }

    private fun addSchedule(scheduleKinds : Int, scheduleTime : String, scheduleData : ScheduleResponse){
        var index = CalendarUtils.getDayIndex(DateTime(millis), DateTime.parse(scheduleTime))
        if( 0 <= index && index < scheduleArray.size) {
            scheduleArray[index].add(Pair(scheduleKinds, scheduleData))
        }
    }

    private fun updateSchedule(scheduleResponse : ArrayList<ScheduleResponse>) {
        scheduleResponse.forEach { scheduleData ->
            scheduleData.ipoStartDate?.let { ipoStartDate ->
                addSchedule(1, ipoStartDate, scheduleData)
            }
            scheduleData.ipoEndDate?.let { ipoEndDate ->
                addSchedule(2, ipoEndDate, scheduleData)
            }
            scheduleData.ipoDebutDate?.let { ipoDebutDate ->
                addSchedule(3, ipoDebutDate, scheduleData)
            }
            scheduleData.ipoRefundDate?.let { ipoRefundDate ->
                addSchedule(4, ipoRefundDate, scheduleData)
            }
        }
    }


    // 달력 일자 Bind
    private fun onBindDay() {
        var firstDayOfMonth = DateTime(millis)
        for ((i, gridLayout) in rows.withIndex()) {
            for (j in 0 until 7) {
                with((gridLayout[j] as LinearLayout)[0] as TextView) {
                    setDayView(this, monthList[(i * 7) + j], DateTime(millis))
                }
            }
        }
    }

    // 스케줄 라벨 Bind
    private fun onBindLabelView() {
        for ((i, gridLayout) in rows.withIndex()) {
            // 갱신시 이전 라벨뷰 삭제 (달력 일자 View 제외)
            gridLayout.removeViews(7,gridLayout.childCount-7)
            // 월요일부터 금요일까지의 스케줄 라벨만 추가
            for(j in 1 until 6) {
                var cnt = 1
                for (scheduleData in scheduleArray[i*7+j]) {
                    gridLayout.addView(CalendarLabelView(gridLayout.context).apply {
                        // 1. 청약 일 추가
                        if (scheduleViewModel.scheduleFilteringList.value!![1]) {
                        }
                        addScheduleLabel(scheduleData.second, scheduleData.first, cnt, j, j)
                    }
                }
            }
        }
    }

    private fun setDayView(view: TextView, date: DateTime, firstDayOfMonth: DateTime) {
        with(view) {
            if (CalendarUtils.isSameDay(date)) setBackgroundResource(R.drawable.bg_circle_24)
            else background = null
            text = date.dayOfMonth.toString()
            alpha = if (!CalendarUtils.isSameMonth(date, firstDayOfMonth)) 0.3F else 1.0F
        }
    }

    companion object {
        private const val MILLIS = "MILLIS"
        fun newInstance(millis: Long) = CalendarFragment().apply {
            arguments = Bundle().apply {
                putLong(MILLIS, millis)
            }
        }
    }
}


