package com.psw9999.gongmozootopia.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.data.ScheduleResponse
import com.psw9999.gongmozootopia.databinding.HolderScheduleListHeaderBinding
import com.psw9999.gongmozootopia.databinding.HolderScheduleListItemBinding
import org.joda.time.DateTime

class CalendarListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var scheduleDataList = mutableListOf<Pair<Int, ScheduleResponse>>()
    var selectedDay: String = DateTime().toString("YYYY년 MM월 dd일")
    private lateinit var mClickListener : StockOnClickListener
    private lateinit var mContext : Context

    interface StockOnClickListener {
        fun stockClick(ipoIndex : Long)
    }

    fun setOnClickListener(mListener : StockOnClickListener) {
        mClickListener = mListener
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        return when(viewType) {
            TYPE_HEADER -> ScheduleHeaderViewHolder(HolderScheduleListHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else -> ScheduleItemViewHolder(HolderScheduleListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScheduleHeaderViewHolder -> {
                holder.binding.textViewSelectedDay.text = selectedDay
                holder.binding.textViewScheduleCnt.text = "${scheduleDataList.size}"
            }
            is ScheduleItemViewHolder -> {
                with(scheduleDataList[position-1]) {
                    holder.binding.textViewStock.text = second.stockName
                    holder.binding.textViewKinds.text = second.stockKinds
                    holder.binding.textViewScheduleType.backgroundTintList = when (first) {
                        1,2  -> ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.CalendarLabel_IpoDay))
                        3 -> ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.CalendarLabel_IpoRefundDay))
                        4 -> ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.CalendarLabel_IpoDebutDay))
                        else -> ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.CalendarLabel_IpoForecastDay))
                    }
                    holder.binding.textViewScheduleType.text = when (first) {
                        1,2 -> "청약일"
                        3 -> "환불일"
                        4 -> "상장일"
                        else -> "수요예측일"
                    }
                    holder.binding.holderScheduleListItem.setOnClickListener {
                        if (position != RecyclerView.NO_POSITION) {
                            mClickListener.stockClick(second.ipoIndex)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return scheduleDataList.size + 1
    }

    class ScheduleHeaderViewHolder(val binding: HolderScheduleListHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    class ScheduleItemViewHolder(val binding : HolderScheduleListItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val TYPE_HEADER : Int = 0
        const val TYPE_ITEM : Int = 1
    }
}
