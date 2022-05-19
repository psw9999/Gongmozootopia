package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.psw9999.gongmozootopia.Adapter.CalendarAdapter
import com.psw9999.gongmozootopia.Adapter.CalendarViewAdapter
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.today
import com.psw9999.gongmozootopia.viewModel.ScheduleViewModel
import com.psw9999.gongmozootopia.databinding.FragmentCalendarBinding
import org.joda.time.DateTime

class TempFragment : Fragment() {
    lateinit var binding : FragmentCalendarBinding
    lateinit var calendarAdapter: CalendarAdapter
    private val scheduleViewModel : ScheduleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)
        calendarAdapter = CalendarAdapter(this)
        with(binding) {
            lifecycleOwner = this@TempFragment
            binding.viewModel = scheduleViewModel
            viewPager2Calendar.adapter = calendarAdapter
            viewPager2Calendar.isUserInputEnabled = false
            viewPager2Calendar.setCurrentItem(CalendarViewAdapter.START_POSITION, false)
        }
        onClickSetting()
        return binding.root
    }

    private fun onClickSetting() {
        with(binding) {
            imgBtnPriviousBtn.setOnClickListener {
                viewPager2Calendar.currentItem--
                scheduleViewModel.currentScheduleMoth.value =
                    (DateTime(today).plusMonths(viewPager2Calendar.currentItem - CalendarAdapter.START_POSITION)).toString("yyyy년 MM월")
            }
            imgBtnNextBtn.setOnClickListener {
                viewPager2Calendar.currentItem++
                scheduleViewModel.currentScheduleMoth.value =
                    (DateTime(today).plusMonths(viewPager2Calendar.currentItem - CalendarAdapter.START_POSITION)).toString("yyyy년 MM월")
            }

            chipIpoFilter.setOnCheckedChangeListener { chip, isChecked ->
                scheduleViewModel.scheduleFilteringList.value!![1] = isChecked
                scheduleViewModel.scheduleFilteringList.value!![2] = isChecked
                scheduleViewModel.scheduleFilteringList.postValue(scheduleViewModel.scheduleFilteringList.value)
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