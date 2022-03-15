package com.psw9999.ipo_alarm.UI.Fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.psw9999.ipo_alarm.Adapter.CalendarAdapter
import com.psw9999.ipo_alarm.data.CalendarData
import com.psw9999.ipo_alarm.databinding.FragmentCalendarBinding
import java.util.*


class CalendarFragment : Fragment() {

    private lateinit var calendarAdapter : CalendarAdapter
    public lateinit var binding : FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater,container,false)
        initCalendarAdapter()
        return binding.root
    }

    private fun initCalendarAdapter() {
        calendarAdapter = CalendarAdapter(this)
        binding.viewPagerCalendar.adapter = calendarAdapter
        binding.viewPagerCalendar.setCurrentItem(CalendarAdapter.START_POSITION, false)
    }
}