package com.psw9999.gongmozootopia.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.customView.UnderwriterView
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockListItem
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.databinding.HolderCommentListHeaderBinding
import com.psw9999.gongmozootopia.databinding.HolderCommentListItemBinding
import com.psw9999.gongmozootopia.databinding.HolderStockBinding
import com.psw9999.gongmozootopia.databinding.HolderStockHeaderBinding

const val TAG = "StockListPagingAdapter"

class StockListPagingAdapter :
    PagingDataAdapter<StockListItem, RecyclerView.ViewHolder>(diffCallback = differ) {
    private var stockFirmEnableMap = mapOf<String,Boolean>()
    private var stockFollowingList = listOf<Long>()
    lateinit var mStockClickListener : OnStockClickListener

    companion object {
        private val differ = object : DiffUtil.ItemCallback<StockListItem>() {
            override fun areItemsTheSame(oldItem: StockListItem, newItem: StockListItem): Boolean {
                return (oldItem is StockListItem.StockItem && newItem is StockListItem.StockItem &&
                        oldItem.stock.ipoIndex == newItem.stock.ipoIndex)
            }

            override fun areContentsTheSame(oldItem: StockListItem, newItem: StockListItem): Boolean {
                return oldItem == newItem
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.holder_stock_header -> StockHeaderViewHolder(
                HolderStockHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> StockViewHolder(
                HolderStockBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is StockListItem.StockItem -> R.layout.holder_stock
            is StockListItem.SeparatorItem -> R.layout.holder_stock_header
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val stockItem = getItem(position)
        stockItem.let {
            when(stockItem) {
                is StockListItem.SeparatorItem -> (holder as StockHeaderViewHolder).binding(stockItem.headerText)
                is StockListItem.StockItem -> (holder as StockViewHolder).binding(stockItem.stock)
            }
        }
    }

    inner class StockHeaderViewHolder(val binding: HolderStockHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(headerText : String) {
            binding.separatorDescription.text = headerText
        }
    }

    inner class StockViewHolder(val binding : HolderStockBinding) : RecyclerView.ViewHolder(binding.root) {
//        init {
//            // 팔로잉 클릭 리스너
//            binding.imageViewFavorit.setOnClickListener {
//                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
//                    getItem(bindingAdapterPosition)?.let {
//                        mStockClickListener.stockFollowingClick(
//                            FollowingResponse(it.ipoIndex, it.stockName, it.ipoIndex in stockFollowingList))
//                    }
//                }
//            }
//            // 카드 클릭 리스너
//            binding.cardViewStock.setOnClickListener {
//                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
//                    getItem(bindingAdapterPosition)?.let {
//                        mStockClickListener.stockCardClick(it.ipoIndex)
//                    }
//                }
//            }
//        }

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