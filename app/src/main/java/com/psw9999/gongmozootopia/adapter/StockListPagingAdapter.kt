package com.psw9999.gongmozootopia.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.customView.UnderwriterView
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.databinding.HolderStockBinding

const val TAG = "StockListPagingAdapter"

class StockListPagingAdapter :
    PagingDataAdapter<StockResponse, StockListPagingAdapter.StockViewHolder>(diffCallback = differ) {
    private var stockFirmEnableMap = mapOf<String,Boolean>()
    private var stockFollowingList = listOf<Long>()
    lateinit var mStockClickListener : OnStockClickListener

    companion object {
        private val differ = object : DiffUtil.ItemCallback<StockResponse>() {
            override fun areItemsTheSame(oldItem: StockResponse, newItem: StockResponse): Boolean {
                return oldItem.ipoIndex == newItem.ipoIndex
            }

            override fun areContentsTheSame(oldItem: StockResponse, newItem: StockResponse): Boolean {
                return oldItem.stockName == newItem.stockName
            }
        }
    }

    interface OnStockClickListener {
        fun stockFollowingClick(followingResponse: FollowingResponse)
        fun stockCardClick(ipoIndex : Long)
    }

    fun setOnStockClickListener (mListener : OnStockClickListener) {
        this.mStockClickListener = mListener
    }

    fun setFollowingList(followingList : List<Long>) {
        this.stockFollowingList = followingList
        notifyDataSetChanged()
    }

    fun setStockFirmMap(stockFirmFollowing : Map<String,Boolean>) {
        this.stockFirmEnableMap = stockFirmFollowing
        // 수정 필요..
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        var binding = HolderStockBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        getItem(position)?.let { stockData ->
            holder.binding(stockData)
        }
    }

    inner class StockViewHolder(val binding : HolderStockBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // 팔로잉 클릭 리스너
            binding.imageViewFavorit.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition)?.let {
                        mStockClickListener.stockFollowingClick(
                            FollowingResponse(it.ipoIndex, it.stockName, it.ipoIndex in stockFollowingList))
                    }
                }
            }
            // 카드 클릭 리스너
            binding.cardViewStock.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition)?.let {
                        mStockClickListener.stockCardClick(it.ipoIndex)
                    }
                }
            }
        }

        fun binding(stockData : StockResponse) {
            Log.d(TAG, "binding")
            binding.stockItem = stockData
            binding.imageViewFavorit.isSelected = stockData.ipoIndex in stockFollowingList
            with(stockData) {
                binding.chipGroupAlarm.removeAllViews()
                underwriter?.let {
                    for (name in it.split(',')) {
                        binding.chipGroupAlarm.addView(UnderwriterView(binding.chipGroupAlarm.context).apply {
                            this.text = name
                            this.isChecked = stockFirmEnableMap.getOrDefault(name, false)
                        })
                    }
                }
            }
        }
    }
}