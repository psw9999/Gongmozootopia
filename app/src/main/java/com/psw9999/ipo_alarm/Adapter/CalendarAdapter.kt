package com.psw9999.ipo_alarm.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.psw9999.ipo_alarm.UI.Fragment.Calendar.CalendarViewFragment
import com.psw9999.ipo_alarm.UI.Fragment.Calendar.CalendarViewFragment.Companion.newInstance
import com.psw9999.ipo_alarm.UI.Fragment.CalendarFragment
import org.joda.time.DateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CalendarAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    /* 달의 첫 번째 Day timeInMillis*/
    private var start: Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis
    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): CalendarViewFragment {
        val millis = getItemId(position)
        return newInstance(millis)
    }

    override fun getItemId(position: Int): Long
            = DateTime(start).plusMonths(position - START_POSITION).millis

    override fun containsItem(itemId: Long): Boolean {
        val date = DateTime(itemId)
        return date.dayOfMonth == 1 && date.millisOfDay == 0
    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }

}