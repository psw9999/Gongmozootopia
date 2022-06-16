package com.psw9999.gongmozootopia.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.CustomView.UnderwriterView
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.Util.DiffUtilCallback
import com.psw9999.gongmozootopia.databinding.HolderStockBinding

class StockListAdapter : RecyclerView.Adapter<StockListAdapter.StockViewHolder>() {
    private var stockList = listOf<StockResponse>()
    private var stockFirmFollowing = mapOf<String,Boolean>()

    lateinit var mContext : Context
    lateinit var mStockClickListener : OnStockClickListener

    interface OnStockClickListener {
        fun stockFollowingClick(followingResponse: FollowingResponse)
        fun stockCardClick(pos : Int)
    }

    fun setOnStockClickListener (mListener : OnStockClickListener) {
        this.mStockClickListener = mListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : StockListAdapter.StockViewHolder {
        mContext = parent.context
        var binding = HolderStockBinding.inflate(LayoutInflater.from(mContext),parent,false)
        return StockViewHolder(binding)
    }

    override fun getItemCount(): Int = stockList.size

    override fun onBindViewHolder(holder: StockListAdapter.StockViewHolder, position: Int) {
        holder.binding(stockList[position])
    }

    fun setAdapterStockFirmData(stockFirmFollowing : Map<String,Boolean>) {
        this.stockFirmFollowing = stockFirmFollowing
        notifyDataSetChanged()
    }

    fun setStockList(stockList : List<StockResponse>) {
        this.stockList = stockList
    }

    fun updateStockList(stockData : List<StockResponse>?) {
        stockData?.let {
            val diffCallback = DiffUtilCallback(this.stockList, stockData)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            this.stockList = stockData
            diffResult.dispatchUpdatesTo(this@StockListAdapter)
        }
    }

    inner class StockViewHolder(val binding : HolderStockBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // 팔로잉 클릭 리스너
            binding.imageViewFavorit.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mStockClickListener.stockFollowingClick(
                        FollowingResponse(stockList[adapterPosition].ipoIndex, stockList[adapterPosition].stockName, !stockList[adapterPosition].isFollowing))
                        binding.imageViewFavorit.isSelected = !binding.imageViewFavorit.isSelected
                }
            }

            // 카드 클릭 리스너
            binding.cardViewStock.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mStockClickListener.stockCardClick(adapterPosition)
                }
            }
        }

        fun binding(stockData : StockResponse) {
            binding.stockItem = stockData
            with(stockData) {
                binding.chipGroupAlarm.removeAllViews()
                underwriter?.let {
                    for (name in it.split(',')) {
                        binding.chipGroupAlarm.addView(UnderwriterView(mContext).apply {
                            this.text = name
                            if (!stockFirmFollowing.containsKey(name) || (!stockFirmFollowing[name]!!)) {
                                this.setChipBackgroundColorResource(R.color.white)
                                this.setChipStrokeColorResource(R.color.chip_underwriter)
                                this.setTextAppearance(R.style.Chip_Unregistered_StockFirm_TextTheme)
                            } else {
                                this.setChipBackgroundColorResource(R.color.chip_underwriter)
                                this.setChipStrokeColorResource(R.color.chip_underwriter)
                                this.setTextAppearance(R.style.Chip_Registered_StockFirm_TextTheme)
                            }
                        })
                    }
                }
            }
        }
    }
}
