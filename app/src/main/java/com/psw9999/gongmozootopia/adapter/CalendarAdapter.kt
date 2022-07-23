package com.psw9999.gongmozootopia.adapter

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.psw9999.gongmozootopia.data.ScheduleResponse
import com.psw9999.gongmozootopia.ui.fragment.CalendarItemFragment
import org.joda.time.DateTime

class CalendarAdapter(fm : Fragment) : FragmentStateAdapter(fm){

    private var start: Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis
    lateinit var mScheduleClickListener: OnScheduleClickListener

    override fun createFragment(position: Int): Fragment {
        val millis = getItemId(position)
        return CalendarItemFragment.newInstance(millis, mScheduleClickListener)
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