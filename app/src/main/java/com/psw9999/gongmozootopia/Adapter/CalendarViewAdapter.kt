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
import com.psw9999.gongmozootopia.data.StockScheduleResponse
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
import java.lang.Integer.max

class CalendarViewAdapter() : RecyclerView.Adapter<CalendarViewAdapter.CalendarViewHolder>() {

    private var start : Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis
    private val stockScheduleRepository = StockScheduleRepository()
    lateinit var stockScheduleData : Array<MutableList<Pair<Int, String>>>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewAdapter.CalendarViewHolder {
        return CalendarViewHolder(
            HolderCalendarBinding.inflate(
                LayoutInflater.from(parent.context),parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun getItemId(position: Int): Long {
        return DateTime(start).plusMonths(position - START_POSITION).millis
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        var millis = getItemId(position)
        var monthList = getMonthList(DateTime(millis))

        fun addStockSchedule(scheduleKinds : Int, stockName : String, scheduleTime: String){
            if (scheduleTime == "0000-00-00") return
            var index = getDayIndex(monthList[0],DateTime.parse(scheduleTime))
            if( 0 <= index && index < stockScheduleData.size) {
                stockScheduleData[index].add(Pair(scheduleKinds,stockName))
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            var stockScheduleResponse = arrayListOf<StockScheduleResponse>()
            launch {
                stockScheduleResponse = stockScheduleRepository.getScheduleData(
                    monthList[0].toString("yyyy-MM-dd"),
                    monthList.last().toString("yyyy-MM-dd")
                )
                Log.d("dayRange","${monthList[0].toString("yyyy-MM-dd")} ~ ${monthList.last().toString("yyyy-MM-dd")}")
                Log.d("stockScheduleResponse","$stockScheduleResponse")
            }.join()
            withContext(Dispatchers.Default) {
                stockScheduleData = Array(WEEKS_PER_MONTH*7){mutableListOf()}
                stockScheduleResponse.forEach { stockSchedule ->
//                    stockSchedule.ipoForecastDate?.let {
//                        addStockSchedule(0,stockSchedule.stockName,it)
//                    }
                    stockSchedule.ipoStartDate?.let {
                        if (stockSchedule.ipoEndDate != null) {
                            if (getDayIndex(DateTime.parse(it),DateTime.parse(stockSchedule.ipoEndDate)) > 1) {
                                addStockSchedule(2,stockSchedule.stockName,it)
                                addStockSchedule(2,stockSchedule.stockName,stockSchedule.ipoEndDate!!)
                            }
                            else {
                                addStockSchedule(1,stockSchedule.stockName,it)
                            }
                        }
                        else {
                            addStockSchedule(0,stockSchedule.stockName,it)
                        }
                    }
                    stockSchedule.ipoRefundDate?.let {
                        addStockSchedule(3,stockSchedule.stockName,it)
                    }
                    stockSchedule.ipoDebutDate?.let {
                        addStockSchedule(4,stockSchedule.stockName,it)
                    }
                }
                stockScheduleData.forEach {
                    it.sortBy{it.first}
                }
                Log.d("stockScheduleData","${stockScheduleData.contentDeepToString()}")
            }
            withContext(Dispatchers.Main){
                holder.onBind(DateTime(millis), monthList)
            }
        }
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
            binding.textViewCalendarHeadTextView.text = DateTime(firstDayOfMonth).toString("MM월 yyyy")
            for ((i,gridLayout) in rows.withIndex()) {
                gridLayout.removeViews(7,gridLayout.childCount-7)
                for (j in 0 until 7) {
                    with((gridLayout[j] as LinearLayout)[0] as TextView) {
                        setDayView(this, dateList[(i * 7) + j], firstDayOfMonth)
                    }
                }
                var maxScheduleCnt = 0
                stockScheduleData.slice((i*7)..(i*7)+6).forEach {
                    maxScheduleCnt = max(maxScheduleCnt, it.size)
                }

                var befLabelCnt = mutableListOf<Int>()
                for(j in 0 until 7) {
                    var cnt = 1
                    var tempCntList = mutableListOf<Int>()
                    stockScheduleData[(i*7)+j].forEach { scheduleData ->
                        gridLayout.addView(CalendarLabelView(gridLayout.context).apply {
                            while (cnt in befLabelCnt) cnt++
                            if(scheduleData.first != 1) {
                                onBind(scheduleData.second, scheduleData.first, cnt, j, j)
                            }
                            else {
                                onBind(scheduleData.second, scheduleData.first, cnt,j,j+1)
                                tempCntList.add(cnt)
                            }
                            cnt++
                        })
                    }
                    befLabelCnt = tempCntList
                }
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