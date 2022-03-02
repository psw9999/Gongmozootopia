package com.psw9999.ipo_alarm.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.ipo_alarm.R
import com.psw9999.ipo_alarm.data.FollowingData
import com.psw9999.ipo_alarm.databinding.ItemFollowingListBinding
import com.psw9999.ipo_alarm.databinding.ItemFollowingTabBinding

class FollowingAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        val TYPE_HEADER : Int = 0
        val TYPE_ITEM : Int = 1
        val TYPE_FOOTER : Int = 2
    }

    var followingData = mutableListOf<FollowingData>()
    var unvisibleData = mutableListOf<FollowingData>()

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return TYPE_HEADER
        else if (position == followingData.size + 1)
            return TYPE_FOOTER
        else
            return TYPE_ITEM
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            var binding = ItemFollowingTabBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return FollowingTabViewHolder(binding)
        }
            var binding = ItemFollowingListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return FollowingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FollowingTabViewHolder) {
            holder.binding.imageViewArrowDown.setOnClickListener {
                if (unvisibleData.size == 0) {
                    var count = 0
                    while (followingData.size > 0) {
                        unvisibleData.add(followingData.removeAt(followingData.size-1))
                        count++
                    }
                    Log.d("followingData","${followingData.size}")
                    notifyItemRangeRemoved(1, count)
                    holder.binding.imageViewArrowDown.setImageResource(R.drawable.baseline_keyboard_arrow_up_20)
                }else{
                    var count = 0
                    while (unvisibleData.size > 0) {
                        followingData.add(unvisibleData.removeAt(unvisibleData.size-1))
                        count++
                    }
                    Log.d("unvisibleData","${unvisibleData.size}")
                    notifyItemRangeChanged(1, count)
                    holder.binding.imageViewArrowDown.setImageResource(R.drawable.baseline_keyboard_arrow_down_20)
                }
            }
        }else{
            var followingViewHolder : FollowingListViewHolder = holder as FollowingListViewHolder
            val data = followingData[position-1]
            followingViewHolder.onBinding(data)
        }
    }

    override fun getItemCount(): Int {
        return followingData.size + 1
    }

    inner class FollowingTabViewHolder(val binding: ItemFollowingTabBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    inner class FollowingListViewHolder(val binding : ItemFollowingListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBinding(followingData: FollowingData) {
            binding.textViewFollowingDday.text = followingData.stockDday
            binding.textViewFollowingStockName.text = followingData.stockName
        }
    }
}
