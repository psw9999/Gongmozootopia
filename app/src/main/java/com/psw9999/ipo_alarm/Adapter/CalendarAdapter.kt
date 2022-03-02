package com.psw9999.ipo_alarm.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.ipo_alarm.data.CalendarData
import com.psw9999.ipo_alarm.databinding.ItemCalendarBinding

class CalendarAdapter(var rvHeight : Int) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    var calendarData = mutableListOf<CalendarData>()
    lateinit var mItemClickListener: OnItemClickListener

    var selectedItemPos = 0
    var lastItemSelectedPos = 0

    interface OnItemClickListener{
        fun onItemClick(view: View, pos : Int)
    }

    fun setOnItemClickListener(itemClickListener : OnItemClickListener) {
        mItemClickListener = itemClickListener
    }

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
        init{
            binding.viewCalendar.layoutParams.height = rvHeight / 5
        }
//        init {
//            binding.buttonCalendar.setOnClickListener {
//                selectedItemPos = adapterPosition
//                if (lastItemSelectedPos == -1)
//                    lastItemSelectedPos = selectedItemPos
//                else {
//                    notifyItemChanged(lastItemSelectedPos)
//                    lastItemSelectedPos = selectedItemPos
//                }
//                mItemClickListener?.onItemClick(itemView,selectedItemPos)
//                notifyItemChanged(selectedItemPos)
//            }
//        }


        fun setDate(calendarData : CalendarData) {
            Log.d("setDate","setDate")
            binding.textViewDate.text = "${calendarData.date}"
        }

    }


}