package com.psw9999.gongmozootopia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.Util.DiffUtilCallback
import com.psw9999.gongmozootopia.customView.UnderwriterView
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.databinding.HolderStockBinding

class StockListPagingAdapter :
    PagingDataAdapter<StockResponse, StockListPagingAdapter.StockViewHolder>(diffCallback = differ) {
    private var stockFirmFollowing = mapOf<String,Boolean>()
    lateinit var mStockClickListener : OnStockClickListener

    companion object {
        private val differ = object : DiffUtil.ItemCallback<StockResponse>() {
            override fun areItemsTheSame(oldItem: StockResponse, newItem: StockResponse): Boolean {
                return oldItem.ipoIndex == newItem.ipoIndex
            }

            override fun areContentsTheSame(oldItem: StockResponse, newItem: StockResponse): Boolean {
                return oldItem.stockName == newItem.stockName &&
                        oldItem.stockExchange == newItem.stockExchange
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
                        mStockClickListener.stockFollowingClick(FollowingResponse(it.ipoIndex, it.stockName, it.isFollowing))
                        binding.imageViewFavorit.isSelected = !binding.imageViewFavorit.isSelected
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
            binding.stockItem = stockData
            with(stockData) {
                binding.chipGroupAlarm.removeAllViews()
                underwriter?.let {
                    for (name in it.split(',')) {
                        binding.chipGroupAlarm.addView(UnderwriterView(binding.chipGroupAlarm.context).apply {
                            this.text = name
                            this.isChecked = stockFirmFollowing.getOrDefault(name, false)
                        })
                    }
                }
            }
        }
    }
}