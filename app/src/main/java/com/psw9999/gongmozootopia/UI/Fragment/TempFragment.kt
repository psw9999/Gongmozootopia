package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.psw9999.gongmozootopia.Adapter.CalendarAdapter
import com.psw9999.gongmozootopia.Adapter.CalendarViewAdapter
import com.psw9999.gongmozootopia.databinding.FragmentCalendarBinding


class TempFragment : Fragment() {
    lateinit var viewBinding : FragmentCalendarBinding
    lateinit var calendarAdapter: CalendarAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCalendarBinding.inflate(inflater,container,false)
        calendarAdapter = CalendarAdapter(this)
        with(viewBinding) {
            viewPager2Calendar.adapter = calendarAdapter
            viewPager2Calendar.isUserInputEnabled = false
            viewPager2Calendar.setCurrentItem(CalendarViewAdapter.START_POSITION, false)
        }
        onClickSetting()
        return viewBinding.root
    }

    private fun onClickSetting() {
        with(viewBinding) {
            imgBtnPriviousBtn.setOnClickListener {
                viewPager2Calendar.currentItem--
            }
            imgBtnNextBtn.setOnClickListener {
                viewPager2Calendar.currentItem++
            }
            chipIpoFilter.setOnClickListener {
                calendarAdapter.notifyDataSetChanged()
            }
        }
    }

}