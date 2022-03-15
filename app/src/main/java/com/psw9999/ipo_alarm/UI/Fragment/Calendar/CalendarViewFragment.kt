package com.psw9999.ipo_alarm.UI.Fragment.Calendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.psw9999.ipo_alarm.Adapter.CalendarAdapter
import com.psw9999.ipo_alarm.databinding.FragmentCalendarViewBinding
import com.psw9999.ipo_alarm.util.CalendarUtils.Companion.getMonthList
import org.joda.time.DateTime

class CalendarViewFragment : Fragment() {
    private var millis: Long = 0L
    private lateinit var binding : FragmentCalendarViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            millis = it.getLong(MILLIS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarViewBinding.inflate(inflater,container,false)
        binding.millis.text = DateTime(millis).toString("MM월 yyyy")
        binding.calendarView.initCalendar(DateTime(millis), getMonthList(DateTime(millis)))
        return binding.root
    }

    companion object {
        private const val MILLIS = "MILLIS"
        fun newInstance(millis: Long) = CalendarViewFragment().apply {
            arguments = Bundle().apply {
                putLong(MILLIS,millis)
            }
        }
    }

}