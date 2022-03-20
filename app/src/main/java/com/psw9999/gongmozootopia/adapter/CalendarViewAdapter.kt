package com.psw9999.gongmozootopia.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.databinding.HolderCalendarBinding
import com.psw9999.gongmozootopia.util.CalendarUtils.Companion.getMonthList
import com.psw9999.gongmozootopia.util.CalendarUtils.Companion.isSameMonth
import org.joda.time.DateTime
import java.util.*

class CalendarViewAdapter : RecyclerView.Adapter<CalendarViewAdapter.CalendarViewHolder>() {

    private var start : Long = DateTime().withDayOfMonth(1).withTimeAtStartOfDay().millis

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewAdapter.CalendarViewHolder {
        return CalendarViewHolder(
            HolderCalendarBinding.inflate(
                LayoutInflater.from(parent.context),parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun getItemId(position: Int): Long {
        return DateTime(start).plusMonths(position - START_POSITION).millis
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        var millis = getItemId(position)
        holder.onBind(DateTime(millis), getMonthList(DateTime(millis)))
    }

    inner class CalendarViewHolder(val binding : HolderCalendarBinding) : RecyclerView.ViewHolder(binding.root) {
        private val rows: List<GridLayout>
            get() {
                return arrayListOf<View>().apply {
                    itemView.findViewsWithText(this, "row", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
                }.map {
                    it as GridLayout
                }
            }

        fun onBind(firstDayOfMonth: DateTime, dateList : List<DateTime>) {
            for (gridLayout in rows) {
                for (i in 0 until 7) {
                    for(j in 0 until 7) {
                        with((gridLayout[i] as LinearLayout)[0] as TextView) {
                            setDayView(this,dateList[(i*7)+j],firstDayOfMonth)
                        }
                    }
                }
            }
        }

        private fun setDayView(view: TextView, date: DateTime, firstDayOfMonth: DateTime) {
            with(view) {
                background = null
                text = date.dayOfMonth.toString()
                alpha = if (!isSameMonth(date, firstDayOfMonth)) 1.0F else 0.3F
//                val outValue = TypedValue()
//                setTextColor(context.getColor(when(date.dayOfWeek) {
//                    Calendar.SATURDAY -> {
//                        context.theme.resolveAttribute(R.attr.colorSaturday, outValue, true)
//                        outValue.resourceId
//                    }
//                    Calendar.SUNDAY -> {
//                        context.theme.resolveAttribute(R.attr.colorSunday, outValue, true)
//                        outValue.resourceId
//                    }
//                    else -> {
//                        context.theme.resolveAttribute(R.attr.colorWeek, outValue, true)
//                        outValue.resourceId
//                    }
//                }))
            }
        }

    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }


}