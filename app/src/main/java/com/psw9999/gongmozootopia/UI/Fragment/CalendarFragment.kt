package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.gongmozootopia.Adapter.CalendarViewAdapter
import com.psw9999.gongmozootopia.databinding.FragmentCalendarBinding
import org.joda.time.DateTime

class CalendarFragment : Fragment() {

    lateinit var viewBinding : FragmentCalendarBinding
    lateinit var calendarViewAdapter: CalendarViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCalendarBinding.inflate(inflater,parent,false)
        calendarViewAdapter = CalendarViewAdapter()
        with(viewBinding) {
            viewPager2Calendar.adapter = calendarViewAdapter
            viewPager2Calendar.isUserInputEnabled = false
            viewPager2Calendar.setCurrentItem(CalendarViewAdapter.START_POSITION, false)
            textViewCalendarHeadTextView.text = calendarViewAdapter.getCurrentMonth(viewPager2Calendar.currentItem)
        }
        onClickSetting()
        return viewBinding.root
    }

    private fun onClickSetting() {
        with(viewBinding) {
            imgBtnPriviousBtn.setOnClickListener {
                viewPager2Calendar.currentItem--
                textViewCalendarHeadTextView.text = calendarViewAdapter.getCurrentMonth(viewPager2Calendar.currentItem)
            }
            imgBtnNextBtn.setOnClickListener {
                viewPager2Calendar.currentItem++
                textViewCalendarHeadTextView.text = calendarViewAdapter.getCurrentMonth(viewPager2Calendar.currentItem)
            }
            chipIpoFilter.setOnCheckedChangeListener { chip, isChecked ->
                calendarViewAdapter.filteringList[1] = isChecked
                calendarViewAdapter.filteringList[2] = isChecked
                calendarViewAdapter.notifyDataSetChanged()
            }
            chipRefundFilter.setOnCheckedChangeListener { chip, isChecked ->
                calendarViewAdapter.filteringList[3] = isChecked
                calendarViewAdapter.notifyDataSetChanged()
            }
            chipDebutFilter.setOnCheckedChangeListener { chip, isChecked ->
                calendarViewAdapter.filteringList[4] = isChecked
                calendarViewAdapter.notifyDataSetChanged()
            }
        }
    }
}