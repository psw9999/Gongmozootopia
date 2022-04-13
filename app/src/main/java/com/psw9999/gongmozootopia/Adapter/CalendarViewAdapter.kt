package com.psw9999.gongmozootopia.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.CustomView.CalendarLabelView
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.databinding.HolderCalendarBinding
import com.psw9999.gongmozootopia.util.CalendarUtils.Companion.getMonthList
import com.psw9999.gongmozootopia.util.CalendarUtils.Companion.isSameDay
import com.psw9999.gongmozootopia.util.CalendarUtils.Companion.isSameMonth
import org.joda.time.DateTime

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
        Log.d("onBindViewHolder",DateTime(millis).toString("MM월 yyyy"))
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
            binding.textViewCalendarHeadTextView.text = DateTime(firstDayOfMonth).toString("MM월 yyyy")
            for ((i,gridLayout) in rows.withIndex()) {
                gridLayout.removeViews(7,gridLayout.childCount-7)
                for (j in 0 until 6) {
                        with((gridLayout[j] as LinearLayout)[0] as TextView) {
                            setDayView(this, dateList[(i * 7) + j], firstDayOfMonth)
                            if (isSameDay(dateList[(i * 7) + j])) {
                                gridLayout.addView(CalendarLabelView(context).apply { onBind(0, 2) })
                                gridLayout.addView(CalendarLabelView(context).apply { onBind(2, 3) })
                            }
                        }
                    }
                }
            }


        private fun setDayView(view: TextView, date: DateTime, firstDayOfMonth: DateTime) {
            with(view) {
                if (isSameDay(date)) setBackgroundResource(R.drawable.bg_circle_24)
                else background = null
                text = date.dayOfMonth.toString()
                alpha = if (!isSameMonth(date, firstDayOfMonth)) 0.3F else 1.0F
            }
        }
    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }


}