package com.psw9999.gongmozootopia.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.data.ScheduleResponse
import com.psw9999.gongmozootopia.databinding.HolderScheduleListHeaderBinding
import org.joda.time.DateTime

class CalendarListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var scheduleDataList = mutableListOf<Pair<Int, ScheduleResponse>>()
    var selectedDay: String = DateTime().toString("YYYY년 MM월 dd일")

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_HEADER -> ScheduleHeaderViewHolder(HolderScheduleListHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else -> ScheduleItemViewHolder(HolderScheduleListHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScheduleHeaderViewHolder -> {
                holder.binding.textViewSelectedDay.text = selectedDay
            }
        }
    }

    override fun getItemCount(): Int {
        return scheduleDataList.size + 1
    }

    inner class ScheduleHeaderViewHolder(val binding: HolderScheduleListHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    inner class ScheduleItemViewHolder(val binding : HolderScheduleListHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    companion object {
        const val TYPE_HEADER : Int = 0
        const val TYPE_ITEM : Int = 1
    }
}
