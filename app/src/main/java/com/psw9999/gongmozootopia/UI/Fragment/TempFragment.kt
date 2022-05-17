package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.psw9999.gongmozootopia.Adapter.CalendarAdapter
import com.psw9999.gongmozootopia.Adapter.CalendarViewAdapter
import com.psw9999.gongmozootopia.ViewModel.ScheduleViewModel
import com.psw9999.gongmozootopia.ViewModel.StockFollowingViewModel
import com.psw9999.gongmozootopia.databinding.FragmentCalendarBinding
import java.util.Observer

class TempFragment : Fragment() {
    lateinit var viewBinding : FragmentCalendarBinding
    lateinit var calendarAdapter: CalendarAdapter
    private val scheduleViewModel : ScheduleViewModel by viewModels()

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
            chipIpoFilter.setOnCheckedChangeListener { chip, isChecked ->
                scheduleViewModel.scheduleFilteringList.value!![1] = isChecked
                scheduleViewModel.scheduleFilteringList.value!![2] = isChecked
            }
            chipRefundFilter.setOnCheckedChangeListener { chip, isChecked ->
                scheduleViewModel.scheduleFilteringList.value!![3] = isChecked
            }
            chipDebutFilter.setOnCheckedChangeListener { chip, isChecked ->
                scheduleViewModel.scheduleFilteringList.value!![4] = isChecked
            }
        }
    }

}