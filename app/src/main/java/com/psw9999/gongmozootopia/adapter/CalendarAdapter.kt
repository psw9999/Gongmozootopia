package com.psw9999.gongmozootopia.adapter

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Repository.StockScheduleRepository
import com.psw9999.gongmozootopia.Util.CalendarUtils
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.getDayIndex
import com.psw9999.gongmozootopia.customView.CalendarLabelView
import com.psw9999.gongmozootopia.data.ScheduleResponse
import com.psw9999.gongmozootopia.databinding.HolderCalendarBinding
import com.psw9999.gongmozootopia.viewModel.ConfigurationViewModel
import com.psw9999.gongmozootopia.viewModel.ScheduleViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class CalendarAdapter(fm : Fragment) : FragmentStateAdapter(fm){

    private var start: Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis
    lateinit var mScheduleClickListener: OnScheduleClickListener

    override fun createFragment(position: Int): Fragment {
        val millis = getItemId(position)
        return CalendarFragment.newInstance(millis, mScheduleClickListener)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun getItemId(position: Int): Long =
        DateTime(start).plusMonths(position - START_POSITION).millis

    override fun containsItem(itemId: Long): Boolean {
        val date = DateTime(itemId)
        return date.dayOfMonth == 1 && date.millisOfDay == 0
    }

    interface OnScheduleClickListener : Parcelable {
        var temp : Int
        fun dayClick(clickedDay : MutableList<Pair<Int, ScheduleResponse>>)
    }

    fun setOnScheduleClickListener (mListener : OnScheduleClickListener) {
        this.mScheduleClickListener = mListener
    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }
}

class CalendarFragment : Fragment() {
    private var millis : Long = 0L
    private val scheduleArray = Array(CalendarUtils.WEEKS_PER_MONTH *7){mutableListOf<Pair<Int,ScheduleResponse>>()}
    private lateinit var rows: List<GridLayout>
    private lateinit var monthList : List<DateTime>
    private lateinit var mClickListener: CalendarAdapter.OnScheduleClickListener

    private val scheduleViewModel : ScheduleViewModel by activityViewModels()
    private val configurationViewModel : ConfigurationViewModel by viewModels()
    private var kindFilteringArray = arrayOf("공모주", "실권주", "스팩주")
    private var scheduleFilteringArray = arrayOf(true,true,true,true,true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            millis = it.getLong(MILLIS)
            mClickListener = it.getParcelable(SCHEDULE_CLICK)!!
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
        CoroutineScope(Dispatchers.Main).launch {
            // 1. 캘린더의 첫날부터 마지막날까지의 일정 조회
            val deferredScheduleResponse = async(Dispatchers.IO) {
                StockScheduleRepository().getScheduleData(
                    monthList[0].toString("yyyy-MM-dd"),
                    monthList.last().toString("yyyy-MM-dd")
                )
            }

            // 2. 캘린더 일정 분류
            val updateScheduleJob = async(Dispatchers.Default) {
                val scheduleResponse = deferredScheduleResponse.await()
                updateSchedule(scheduleResponse)
                scheduleArray.forEach { it ->
                    it.sortBy { it.first }
                }
                true
            }

            // 3. Bind 함수 실행
            launch {
                var temp = updateScheduleJob.await()
                onBindDay()
                onBindLabel()
            }
        }

        scheduleViewModel.isIpoDayEnabled.observe(viewLifecycleOwner, Observer {
            scheduleFilteringArray[1] = it
            scheduleFilteringArray[2] = it
            onBindLabel()
        })

        scheduleViewModel.isRefundDayEnabled.observe(viewLifecycleOwner, Observer {
            scheduleFilteringArray[3] = it
            onBindLabel()
        })

        scheduleViewModel.isDebutDayEnabled.observe(viewLifecycleOwner, Observer {
            scheduleFilteringArray[4] = it
            onBindLabel()
        })

//        configurationViewModel.isForfeitedEnabled.observe(viewLifecycleOwner, Observer {
//            if(it) kindFilteringArray[1] = "실권주"
//            else kindFilteringArray[1] = ""
//            onBindLabel()
//        })
//
//        configurationViewModel.isSpacEnabled.observe(viewLifecycleOwner, Observer {
//            if(it) kindFilteringArray[2] = "스팩주"
//            else kindFilteringArray[2] = ""
//            onBindLabel()
//        })
        setOnClickListener()
        return viewBinding.root
    }

    private fun setOnClickListener() {
        rows.forEachIndexed { y, view ->
            val detector = GestureDetector(context, object : GestureDetector.OnGestureListener {
                override fun onDown(e: MotionEvent?): Boolean {
                    return true
                }

                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val x = (e.x / (view.width / 7)).toInt()
                    scheduleViewModel.selectedDay.value = monthList[(y*7)+x].toString("yyyy년 MM월 dd일")
                    mClickListener.dayClick(listFiltering(scheduleArray[y*7+x]))
                    return true
                }

                override fun onFling(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent?) {
                }

                override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                    return true
                }

                override fun onShowPress(e: MotionEvent?) {
                }
            })
            view.setOnTouchListener { _, event ->
                view.performClick()
                detector.onTouchEvent(event)
            }
        }
    }

    private fun addSchedule(scheduleKinds : Int, scheduleTime : String, scheduleData : ScheduleResponse){
        var index = getDayIndex(monthList.first(), DateTime.parse(scheduleTime))
        if( 0 <= index && index < scheduleArray.size) {
            scheduleArray[index].add(Pair(scheduleKinds, scheduleData))
        }
    }

    private fun updateSchedule(scheduleResponse : ArrayList<ScheduleResponse>) {
        scheduleResponse.forEach { scheduleData ->
            scheduleData.ipoStartDate?.let { ipoStartDate ->
                scheduleData.ipoEndDate?.let { ipoEndDate ->
                    // 청약 시작일과 청약 마감일이 하루 이상인 경우 분리하여 표시
                    if (getDayIndex(DateTime.parse(ipoStartDate), DateTime.parse(ipoEndDate)) > 1) {
                        addSchedule(2, ipoStartDate, scheduleData)
                        addSchedule(2, ipoEndDate, scheduleData)
                    }
                    else {
                        addSchedule(1, ipoStartDate, scheduleData)
                        addSchedule(1, ipoEndDate, scheduleData)
                    }
                }
            }
            scheduleData.ipoDebutDate?.let { ipoDebutDate ->
                addSchedule(3, ipoDebutDate, scheduleData)
            }
            scheduleData.ipoRefundDate?.let { ipoRefundDate ->
                addSchedule(4, ipoRefundDate, scheduleData)
            }
        }
    }


    // 달력 일자 Bind
    private fun onBindDay() {
        for ((i, gridLayout) in rows.withIndex()) {
            for (j in 0 until 7) {
                with((gridLayout[j] as LinearLayout)[0] as TextView) {
                    setDayView(this, monthList[(i * 7) + j], DateTime(millis))
                }
            }
        }
    }

    // 스케줄 라벨 Bind
    private fun onBindLabel() {
        Log.d("CalendarFragment","Binding")
        for ((i, gridLayout) in rows.withIndex()) {
            // 갱신시 이전 라벨뷰 삭제 (달력 일자 View 제외)
            gridLayout.removeViews(7,gridLayout.childCount-7)
            var befLabelView = Array<Long>(6){-1L}
            // 월요일부터 금요일까지의 스케줄 라벨만 추가
            for(j in 1 until 6) {
                var cnt = 1
                var tempLabelView = Array<Long>(6){-1L}
                for ((scheduleKinds, scheduleData) in listFiltering(scheduleArray[i*7+j])) {
                    while (befLabelView[cnt] != -1L) {
                        cnt++
                        if (cnt >= 5) break
                    }
                    if (cnt >= 5) break
                    // 필터링이 안된 경우에만 진행
                    if (scheduleKinds == 1) {
                        if (!befLabelView.contains(scheduleData.ipoIndex)) {
                            gridLayout.addView(CalendarLabelView(gridLayout.context).apply {
                                addScheduleLabel(scheduleData.stockName, scheduleKinds, cnt, j, j + 1)
                            })
                            tempLabelView[cnt] = scheduleData.ipoIndex
                        } else continue
                    } else {
                        gridLayout.addView(CalendarLabelView(gridLayout.context).apply {
                            addScheduleLabel(scheduleData.stockName, scheduleKinds, cnt, j, j)
                        })
                    }
                    cnt++
                }
                befLabelView = tempLabelView
            }
        }
    }

    private fun listFiltering(befData : MutableList<Pair<Int, ScheduleResponse>>) : MutableList<Pair<Int, ScheduleResponse>>{
        return befData.filter { data ->
            data.second.stockKinds in kindFilteringArray && scheduleFilteringArray[data.first]
        }.toMutableList()
    }

    private fun setDayView(view: TextView, date: DateTime, firstDayOfMonth: DateTime) {
        with(view) {
            if (CalendarUtils.isSameDay(date)) {
                view.setTextColor(context.getColor(R.color.white))
                setBackgroundResource(R.drawable.bg_circle_24)
            }
            else background = null
            text = date.dayOfMonth.toString()
            alpha = if (!CalendarUtils.isSameMonth(date, firstDayOfMonth)) 0.3F else 1.0F
        }
    }

    companion object {
        private const val MILLIS = "MILLIS"
        private const val SCHEDULE_CLICK = "SCHEDULE_CLICK"
        fun newInstance(millis: Long, scheduleClickListener: CalendarAdapter.OnScheduleClickListener) = CalendarFragment().apply {
            arguments = Bundle().apply {
                putLong(MILLIS, millis)
                putParcelable(SCHEDULE_CLICK, scheduleClickListener)
            }
        }
    }
}