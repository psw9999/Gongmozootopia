package com.psw9999.ipo_alarm.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

//class CalendarAdapter(var rvHeight : Int) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

//    var calendarData = mutableListOf<CalendarData>()
//    lateinit var mItemClickListener: OnItemClickListener
//
//    var selectedItemPos = 0
//    var lastItemSelectedPos = 0
//
//    interface OnItemClickListener{
//        fun onItemClick(view: View, pos : Int)
//    }
//
//    fun setOnItemClickListener(itemClickListener : OnItemClickListener) {
//        mItemClickListener = itemClickListener
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CalendarViewHolder {
//        var binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.context),parent,false)
//        return CalendarViewHolder(binding)
//    }
//
//    override fun getItemCount(): Int {
//        return calendarData.size
//    }
//
//    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
//        val date = calendarData[position]
//        holder.setDate(date)
//    }
//
//    inner class CalendarViewHolder(val binding : ItemCalendarBinding) : RecyclerView.ViewHolder(binding.root) {
//        init{
//            binding.viewCalendar.layoutParams.height = rvHeight / 5
//        }
////        init {
////            binding.buttonCalendar.setOnClickListener {
////                selectedItemPos = adapterPosition
////                if (lastItemSelectedPos == -1)
////                    lastItemSelectedPos = selectedItemPos
////                else {
////                    notifyItemChanged(lastItemSelectedPos)
////                    lastItemSelectedPos = selectedItemPos
////                }
////                mItemClickListener?.onItemClick(itemView,selectedItemPos)
////                notifyItemChanged(selectedItemPos)
////            }
////        }
//
//
//        fun setDate(calendarData : CalendarData) {
//            Log.d("setDate","setDate")
//            binding.textViewDate.text = "${calendarData.date}"
//        }
//
//    }

class CalendarAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    private var startDate : LocalDate = LocalDate.now().withDayOfMonth(1)
    private var stateTime : LocalTime = LocalTime.MIN

    override fun getItemCount(): Int = Int.MAX_VALUE
//    override fun getItemId(position: Int): Int
//        = startDate.get(startDate.plusMonths(position - START_POSITION))

    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
    }

    companion object {
        const val START_POSITION = Int.MAX_VALUE / 2
    }

}