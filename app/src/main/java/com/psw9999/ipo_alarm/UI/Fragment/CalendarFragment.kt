package com.psw9999.ipo_alarm.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.psw9999.ipo_alarm.Adapter.CalendarAdapter
import com.psw9999.ipo_alarm.R
import com.psw9999.ipo_alarm.data.CalendarData
import com.psw9999.ipo_alarm.databinding.FragmentCalendarBinding
import java.util.*


class CalendarFragment : Fragment() {

    var testCalendarData : Int = getCalendarDay(2021,11,26)
    var calendarDateList : MutableList<CalendarData> = loadDayInformation()
    var calendarAdapter = CalendarAdapter()

    lateinit var binding : FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater,container,false)
        initRecyclerView()
        return binding.root
    }

    fun initRecyclerView() {
        calendarAdapter.calendarData = calendarDateList
        binding.recyclerViewCalendar.adapter = calendarAdapter
        binding.recyclerViewCalendar.layoutManager = GridLayoutManager(activity?.baseContext,7, 5)

    }

    fun loadDayInformation() : MutableList<CalendarData> {
        val calendarData : MutableList<CalendarData> = mutableListOf()
        //for ( i in 1..testCalendarData) {
        for ( i in 1..31) {
            calendarData.add(CalendarData(i))
        }
        return calendarData
    }

    fun getCalendarDay(year : Int, month : Int, day : Int) : Int {
        var calendar = Calendar.getInstance()

        // month는 원하는 달의 1을 뺀 값을 전달해야함.
        calendar.set(year,month-1,day)

        // 요일, 일요일 1 ~ 토요일 7
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        // 입력한 월의 마지막 일을 반환
        var lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        return lastDay
    }

}