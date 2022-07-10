package com.psw9999.gongmozootopia.ui.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.psw9999.gongmozootopia.adapter.CalendarAdapter
import com.psw9999.gongmozootopia.adapter.CalendarListAdapter
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.ui.activity.StockInformationActivity
import com.psw9999.gongmozootopia.Util.CalendarUtils
import com.psw9999.gongmozootopia.data.ScheduleResponse
import com.psw9999.gongmozootopia.databinding.FragmentCalendarBinding
import com.psw9999.gongmozootopia.viewModel.ScheduleViewModel
import org.joda.time.DateTime

class CalendarFragment : Fragment() {
    private lateinit var mContext: Context
    lateinit var binding : FragmentCalendarBinding
    lateinit var calendarAdapter: CalendarAdapter
    lateinit var calendarListAdapter: CalendarListAdapter
    private val scheduleViewModel : ScheduleViewModel by activityViewModels()

    private val stockInfoIntent by lazy {
        Intent(mContext, StockInformationActivity::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)
        calendarAdapter = CalendarAdapter(this)
        with(binding) {
            lifecycleOwner = this@CalendarFragment
            binding.viewModel = scheduleViewModel
            viewPager2Calendar.adapter = calendarAdapter
            viewPager2Calendar.isUserInputEnabled = false
            viewPager2Calendar.setCurrentItem(CalendarAdapter.START_POSITION, false)
        }
        initScheduleRecyclerView()
        onClickSetting()
        scheduleViewModel.selectedDay.observe(viewLifecycleOwner, Observer {
            calendarListAdapter.selectedDay = it
            calendarListAdapter.notifyDataSetChanged()
        })
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
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

            chipIpoFilter.setOnCheckedChangeListener { chip, isChecked ->
                scheduleViewModel.isIpoDayEnabled.value = isChecked
            }
            chipRefundFilter.setOnCheckedChangeListener { chip, isChecked ->
                scheduleViewModel.isRefundDayEnabled.value = isChecked
            }
            chipDebutFilter.setOnCheckedChangeListener { chip, isChecked ->
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