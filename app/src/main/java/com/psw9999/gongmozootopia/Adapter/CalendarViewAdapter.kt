package com.psw9999.gongmozootopia.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.CustomView.CalendarLabelView
import com.psw9999.gongmozootopia.data.ScheduleResponse
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Repository.StockScheduleRepository
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.WEEKS_PER_MONTH
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.getDayIndex
import com.psw9999.gongmozootopia.databinding.HolderCalendarBinding
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.getMonthList
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.isSameDay
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.isSameMonth
import kotlinx.coroutines.*
import org.joda.time.DateTime

class CalendarViewAdapter : RecyclerView.Adapter<CalendarViewAdapter.CalendarViewHolder>() {

    private var start : Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis
    private val stockScheduleRepository = StockScheduleRepository()
    private lateinit var scheduleData : Array<MutableList<Pair<Int, String>>>

    // 스케줄 필터링 (수요예측일, 청약일, 청약일, 환불일, 상장일)
    var filteringList = arrayOf(true,true,true,true,true)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        Log.d("recyclerView","onCreateViewHolder")
        return CalendarViewHolder(
            HolderCalendarBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        )
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun getItemId(position: Int): Long {
        Log.d("recyclerView","getItemId")
        return DateTime(start).plusMonths(position - START_POSITION).millis
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        var millis = getItemId(position)
        var monthList = getMonthList(DateTime(millis))
        Log.d("recyclerView","onBindViewHolder")
        CoroutineScope(Dispatchers.Main).launch {
            // 1. 캘린더의 첫날부터 마지막날까지의 일정 조회
            val deferredScheduleResponse = async(Dispatchers.IO) {
                stockScheduleRepository.getScheduleData(
                    monthList[0].toString("yyyy-MM-dd"),
                    monthList.last().toString("yyyy-MM-dd")
                )
            }

            // 2. 캘린더 일정 분류
            val updateScheduleJob = async(Dispatchers.Default) {
                val scheduleResponse = deferredScheduleResponse.await()
                updateSchedule(scheduleResponse, monthList[0])
                scheduleData.forEach { it ->
                    it.sortBy { it.first }
                }
                true
            }

            // 3. Bind 함수 실행
            launch {
                var temp = updateScheduleJob.await()
                holder.onBind(DateTime(millis), monthList)
            }
        }
    }

    private fun addSchedule(scheduleKinds : Int, stockName : String, scheduleTime: String, firstDay : DateTime){
        var index = getDayIndex(firstDay,DateTime.parse(scheduleTime))
        if( 0 <= index && index < scheduleData.size) {
            scheduleData[index].add(Pair(scheduleKinds,stockName))
        }
    }

    private fun updateSchedule(scheduleResponse : ArrayList<ScheduleResponse>, firstDay : DateTime) {
        scheduleData = Array(WEEKS_PER_MONTH*7){mutableListOf()}
        scheduleResponse.forEach { stockSchedule ->
            stockSchedule.ipoStartDate?.let { ipoStartDate->
                stockSchedule.ipoEndDate?.let { ipoEndDate ->
                    if (getDayIndex(DateTime.parse(ipoStartDate),DateTime.parse(stockSchedule.ipoEndDate)) > 1) {
                        addSchedule(2,stockSchedule.stockName,ipoStartDate, firstDay)
                        addSchedule(2,stockSchedule.stockName,ipoEndDate, firstDay)
                    }
                    else {
                        addSchedule(1,stockSchedule.stockName,ipoStartDate, firstDay)
                    }
                }
            }
            stockSchedule.ipoRefundDate?.let { ipoRefundDate ->
                addSchedule(3,stockSchedule.stockName,ipoRefundDate, firstDay)
            }
            stockSchedule.ipoDebutDate?.let { ipoDebutDate ->
                addSchedule(4,stockSchedule.stockName,ipoDebutDate, firstDay)
            }
        }
    }

    //TODO : LiveData로 수정
    fun getCurrentMonth(position: Int) : String {
        return DateTime(getItemId(position)).toString("yyyy년 MM월")
    }
    inner class CalendarViewHolder(val binding : HolderCalendarBinding) : RecyclerView.ViewHolder(binding.root) {
        private val rows: List<GridLayout>
            get() {
                return arrayListOf<View>().apply {
                    itemView.findViewsWithText(this, "row", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
                }.map {
                    it as GridLayout
                }
            }

        fun onBind(firstDayOfMonth: DateTime, dateList : List<DateTime>) {
            //binding.textViewCalendarHeadTextView.text = DateTime(firstDayOfMonth).toString("yyyy년 MM월")
            for ((i,gridLayout) in rows.withIndex()) {
                gridLayout.removeViews(7,gridLayout.childCount-7)
                for (j in 0 until 7) {
                    with((gridLayout[j] as LinearLayout)[0] as TextView) {
                        setDayView(this, dateList[(i * 7) + j], firstDayOfMonth)
                    }
                }

                var befLabelCnt = mutableListOf<Int>()
//                for(j in 0 until 7) {
//                    var cnt = 1
//                    var tempCntList = mutableListOf<Int>()
//                    for (schedule in scheduleData[(i*7)+j]) {
//                        if (!filteringList[schedule.first]) continue
//                        gridLayout.addView(CalendarLabelView(gridLayout.context).apply {
//                            while (cnt in befLabelCnt) cnt++
//                            if(schedule.first != 1) {
//                                onBind(schedule.second, schedule.first, cnt, j, j)
//                            }
//                            else {
//                                onBind(schedule.second, schedule.first, cnt, j, j+1)
//                                tempCntList.add(cnt)
//                            }
//                            cnt++
//                        })
//                    }
//                    befLabelCnt = tempCntList
//                }
            }
        }

        private fun setDayView(view: TextView, date: DateTime, firstDayOfMonth: DateTime) {
            with(view) {
                if (isSameDay(date)) setBackgroundResource(R.drawable.bg_circle_24)
                else background = null
                text = date.dayOfMonth.toString()
                alpha = if (!isSameMonth(date, firstDayOfMonth)) 0.3F else 1.0F
            }
        }
    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }

}