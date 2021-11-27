package com.psw9999.ipo_alarm.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.ipo_alarm.data.CalendarData
import com.psw9999.ipo_alarm.databinding.ItemCalendarBinding

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    var calendarData = mutableListOf<CalendarData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CalendarViewHolder {
        var binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return CalendarViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return calendarData.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = calendarData[position]
        holder.setDate(date)
    }

    inner class CalendarViewHolder(val binding : ItemCalendarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setDate(calendarData : CalendarData) {
            binding.textViewDate.text = "${calendarData.date}"
        }
    }


}