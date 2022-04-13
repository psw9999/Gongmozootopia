package com.psw9999.gongmozootopia.Adapter

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.psw9999.gongmozootopia.Data.StockFollowingResponse
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Data.StockResponse
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.dpToPx
import com.psw9999.gongmozootopia.databinding.ItemStockBinding

class StockListAdapter : RecyclerView.Adapter<StockListAdapter.StockViewHolder>(), Filterable {

    private var filtered : MutableList<StockResponse>? = null
    var stockData = arrayListOf<StockResponse>()
    var stockFirmFollowing = mapOf<String,Boolean>()
    lateinit var mContext : Context
    lateinit var mStockClickListener : OnStockClickListener

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString : List<String> = constraint.split(',')
                var filteringList = arrayListOf<StockResponse>()
                if (charString[1].isEmpty()) {
                    filteringList = stockData
                } else {
                    val filteringWord = charString[1]
                    for (item in stockData) {
                        if(filteringWord == item.stockKinds) filteringList.add(item)
                    }
                }

//                if (charString[2] == "true") {
//                    var temp = ArrayList<StockListResponse>()
//                    for (item in filteringList) {
//                        if(item.isFollowing) temp.add(item)
//                    }
//                    filteringList = temp
//                }

                val filterResults = FilterResults()
                filterResults.values = filteringList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filtered = results?.values as ArrayList<StockResponse>
                notifyDataSetChanged()
            }
        }
    }

    interface OnStockClickListener {
        fun stockFollowingClick(stockFollowingResponse: StockFollowingResponse)
        fun stockCardClick(pos : Int)
    }

    fun setOnStockClickListener (mListener : OnStockClickListener) {
        this.mStockClickListener = mListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : StockListAdapter.StockViewHolder {
        mContext = parent.context
        var binding = ItemStockBinding.inflate(LayoutInflater.from(mContext),parent,false)
        return StockViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (filtered == null) {
            stockData.size
        }else{
            filtered!!.size
        }
    }

    override fun onBindViewHolder(holder: StockListAdapter.StockViewHolder, position: Int) {
        if (filtered == null) {
            holder.setTest(stockData[position])
        }else{
            holder.setTest(filtered!![position])
        }
    }

    fun setAdapterStockFirmData(stockFirmFollowing : Map<String,Boolean>) {
        this.stockFirmFollowing = stockFirmFollowing
        notifyDataSetChanged()
    }

    fun setAdapterStockFollowingData(stockData: ArrayList<StockResponse>) {
        this.stockData = stockData
        notifyDataSetChanged()
    }

    inner class StockViewHolder(val binding : ItemStockBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.imageViewFavorit.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mStockClickListener.stockFollowingClick(
                        StockFollowingResponse(stockData[adapterPosition].ipoIndex, stockData[adapterPosition].stockName, !stockData[adapterPosition].isFollowing))
                }
            }
            binding.cardViewStock.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mStockClickListener.stockCardClick(adapterPosition)
                }
            }
        }

        fun setTest(stockData : StockResponse) {
            with(binding) {
                with(stockData) {
                    //TODO : DataBinding으로 한번에 처리하기
                    textViewStockName.text = stockName
                    imageViewFavorit.isSelected = isFollowing
                    textViewMarketKinds.text = stockExchange
                    textViewStockKinds.text = stockKinds
                    textViewEndDayTitle.text = currentSchedule
                    textViewDday.text = scheduleDday
                    if (scheduleDday == "") textViewDday.visibility = View.GONE
                    else textViewDday.visibility = View.VISIBLE

                    chipGroupAlarm.removeAllViews()
                    //TODO : this.setChipDrawable(chipDrawable) 활용하여 한줄로 줄이기 혹은 커스텀뷰 활용
                    underwriter?.let {
                        for(name in it.split(',')) {
                            val temp = "${name.replace("증권","").replace("투자","").replace("금융","").replace("㈜","")}"
                            chipGroupAlarm.addView(Chip(mContext).apply {
                                this.text = temp
                                this.setEnsureMinTouchTargetSize(false)
                                this.chipStrokeWidth = dpToPx(mContext, 1.5F)
                                this.isClickable = false
                                this.chipCornerRadius = dpToPx(mContext,8F)
                                this.minHeight = 0
                                this.minWidth = 0
                                this.textEndPadding = dpToPx(mContext,2F)
                                this.textStartPadding = dpToPx(mContext,3F)
                                this.updatePadding(0, 0, 0, 0)

                                if (!stockFirmFollowing.containsKey(temp) || (!stockFirmFollowing[temp]!!)) {
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
}
