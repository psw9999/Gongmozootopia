package com.psw9999.gongmozootopia.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.psw9999.gongmozootopia.Util.CalendarUtils
import com.psw9999.gongmozootopia.adapter.CalendarAdapter
import com.psw9999.gongmozootopia.adapter.CalendarListAdapter
import com.psw9999.gongmozootopia.base.BaseFragment
import com.psw9999.gongmozootopia.data.ScheduleResponse
import com.psw9999.gongmozootopia.databinding.FragmentCalendarBinding
import com.psw9999.gongmozootopia.ui.activity.StockInformationActivity
import com.psw9999.gongmozootopia.viewModel.ScheduleViewModel
import org.joda.time.DateTime

class CalendarFragment : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding :: inflate) {
    lateinit var calendarAdapter: CalendarAdapter
    lateinit var calendarListAdapter: CalendarListAdapter
    private val scheduleViewModel : ScheduleViewModel by activityViewModels()

    private val stockInfoIntent by lazy {
        Intent(activityContext, StockInformationActivity::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarAdapter = CalendarAdapter(this)
        with(binding) {
            viewPager2Calendar.adapter = calendarAdapter
            viewPager2Calendar.isUserInputEnabled = false
            viewPager2Calendar.setCurrentItem(CalendarAdapter.START_POSITION, false)
        }
        initScheduleRecyclerView()
        onClickSetting()
    }

    override fun observe() {
        scheduleViewModel.selectedDay.observe(viewLifecycleOwner) {
            calendarListAdapter.selectedDay = it
            calendarListAdapter.notifyDataSetChanged()
        }
    }

    private fun onClickSetting() {
        with(binding) {
            imgBtnPriviousBtn.setOnClickListener {
                viewPager2Calendar.currentItem--
                scheduleViewModel.currentScheduleMoth.value =
                    (DateTime(CalendarUtils.today).plusMonths(viewPager2Calendar.currentItem - CalendarAdapter.START_POSITION)).toString("yyyy년 MM월")
            }

            imgBtnNextBtn.setOnClickListener {
                viewPager2Calendar.currentItem++
                scheduleViewModel.currentScheduleMoth.value =
                    (DateTime(CalendarUtils.today).plusMonths(viewPager2Calendar.currentItem - CalendarAdapter.START_POSITION)).toString("yyyy년 MM월")
            }

            chipIpoFilter.setOnCheckedChangeListener { _, isChecked ->
                scheduleViewModel.isIpoDayEnabled.value = isChecked
            }
            chipRefundFilter.setOnCheckedChangeListener { _, isChecked ->
                scheduleViewModel.isRefundDayEnabled.value = isChecked
            }
            chipDebutFilter.setOnCheckedChangeListener { _, isChecked ->
                scheduleViewModel.isDebutDayEnabled.value = isChecked
            }
        }

        calendarAdapter.setOnScheduleClickListener(object : CalendarAdapter.OnScheduleClickListener {
            override var temp: Int
                get() = TODO("Not yet implemented")
                set(value) {}

            override fun describeContents(): Int {
                return 0
            }

            override fun writeToParcel(dest : Parcel?, flags : Int) {
                dest?.writeInt(1)
            }

            override fun dayClick(clickedDay: MutableList<Pair<Int, ScheduleResponse>>) {
                calendarListAdapter.scheduleDataList = clickedDay
            }
        })
        calendarListAdapter.setOnClickListener(object : CalendarListAdapter.StockOnClickListener {
            override fun stockClick(ipoIndex: Long) {
                stockInfoIntent.apply {
                    putExtra("ipoIndex", ipoIndex)
                }
                startActivity(stockInfoIntent)
            }
        })
    }

    private fun initScheduleRecyclerView() {
        calendarListAdapter = CalendarListAdapter()
        with(binding.recyclerViewScheduleList) {
            adapter = calendarListAdapter
            binding.recyclerViewScheduleList.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}