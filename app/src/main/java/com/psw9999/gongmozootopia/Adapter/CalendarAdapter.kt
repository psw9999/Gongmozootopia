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
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.psw9999.gongmozootopia.CustomView.CalendarLabelView
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Repository.StockScheduleRepository
import com.psw9999.gongmozootopia.Util.CalendarUtils
import com.psw9999.gongmozootopia.data.StockScheduleResponse
import com.psw9999.gongmozootopia.databinding.HolderCalendarBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.util.ArrayList

class CalendarAdapter(fm : Fragment) : FragmentStateAdapter(fm){

    private var start: Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis
    var scheduleFilteringList = intArrayOf(0,0,3,4)

    override fun createFragment(position: Int): Fragment {
        val millis = getItemId(position)
        return CalendarFragment.newInstance(millis, scheduleFilteringList)
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
    private lateinit var filteringList : IntArray
    private lateinit var rows: List<GridLayout>
    private lateinit var monthList : List<DateTime>
    private val stockScheduleRepository = StockScheduleRepository()
    private var stockScheduleData : Array<MutableList<Pair<Int, String>>> = Array(CalendarUtils.WEEKS_PER_MONTH *7){mutableListOf()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            millis = it.getLong(MILLIS)
            filteringList = it.getIntArray(FILTERING)!!
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
        var stockScheduleResponse = arrayListOf<StockScheduleResponse>()

        CoroutineScope(Dispatchers.IO).launch {
            stockScheduleResponse = stockScheduleRepository.getScheduleData(
                monthList[0].toString("yyyy-MM-dd"),
                monthList.last().toString("yyyy-MM-dd")
            )

            withContext(Dispatchers.Default) {
                stockScheduleResponse.forEach { stockSchedule ->
                    stockSchedule.ipoStartDate?.let {
                        if (stockSchedule.ipoEndDate != null) {
                            if (CalendarUtils.getDayIndex(DateTime.parse(it), DateTime.parse(stockSchedule.ipoEndDate)) > 1) {
                                addStockSchedule(2,stockSchedule.stockName,it)
                                addStockSchedule(2,stockSchedule.stockName,stockSchedule.ipoEndDate!!)
                            }
                            else {
                                addStockSchedule(1,stockSchedule.stockName,it)
                            }
                        }
                    }
                    stockSchedule.ipoRefundDate?.let {
                        addStockSchedule(3,stockSchedule.stockName,it)
                    }
                    stockSchedule.ipoDebutDate?.let {
                        addStockSchedule(4,stockSchedule.stockName,it)
                    }
                }
                stockScheduleData.forEach { it ->
                    it.sortBy{it.first}
                }
            }
            withContext(Dispatchers.Main) {
                onBind()
            }
        }

        return viewBinding.root
    }

    private fun addStockSchedule(scheduleKinds : Int, stockName : String, scheduleTime: String){
        var index = CalendarUtils.getDayIndex(monthList[0], DateTime.parse(scheduleTime))
        if( 0 <= index && index < stockScheduleData.size) {
            stockScheduleData[index].add(Pair(scheduleKinds,stockName))
        }
    }

    private fun onBind() {
        var firstDayOfMonth = DateTime(millis)

        for ((i,gridLayout) in rows.withIndex()) {
            //gridLayout.removeViews(7, gridLayout.childCount - 7)
            for (j in 0 until 7) {
                with((gridLayout[j] as LinearLayout)[0] as TextView) {
                    setDayView(this, monthList[(i * 7) + j],firstDayOfMonth)
                }
            }

            var befLabelCnt = mutableListOf<Int>()
            for(j in 0 until 7) {
                var cnt = 1
                var tempCntList = mutableListOf<Int>()
                for (scheduleData in stockScheduleData[(i*7)+j]) {
                    if (!filteringList.contains(scheduleData.first)) continue
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
            if (CalendarUtils.isSameDay(date)) setBackgroundResource(R.drawable.bg_circle_24)
            else background = null
            text = date.dayOfMonth.toString()
            alpha = if (!CalendarUtils.isSameMonth(date, firstDayOfMonth)) 0.3F else 1.0F
        }
    }

    companion object {
        private const val MILLIS = "MILLIS"
        private const val FILTERING = "FILTERING"
        fun newInstance(millis: Long, filteringList : IntArray) = CalendarFragment().apply {
            arguments = Bundle().apply {
                putLong(MILLIS, millis)
                putIntArray(FILTERING, filteringList)
            }
        }
    }

}

