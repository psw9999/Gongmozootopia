package com.psw9999.ipo_alarm.Adapter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.psw9999.ipo_alarm.R
import com.psw9999.ipo_alarm.data.StockListResponse
import com.psw9999.ipo_alarm.databinding.ItemStockBinding


class StockListAdapter : RecyclerView.Adapter<StockListAdapter.StockViewHolder>(), Filterable {

    var stockData = mutableListOf<StockListResponse>()
    private var filteredList = stockData
    var unfilteredStockData = stockData

    lateinit var mContext : Context
    lateinit var mStockClickListener : OnStockClickListener

    fun setNewData(pos : Int, updateData : StockListResponse) {
        if (pos != RecyclerView.NO_POSITION) {
            stockData[pos] = updateData
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                Log.d("charString","$charString")
                filteredList = if (charString.isEmpty()) {
                    unfilteredStockData
                } else {
                    val filteringList = mutableListOf<StockListResponse>()
                    for (item in unfilteredStockData!!) {
                        if(item!!.stockState == charString) filteringList.add(item)
                    }
                    filteringList
                }
                Log.d("filterResult","$filteredList")
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as MutableList<StockListResponse>
                Log.d("publishResults","$stockData")
                notifyDataSetChanged()
            }
        }
    }

    interface OnStockClickListener {
        fun stockFollowingClick(pos : Int)
        fun stockAlarmClick(pos : Int)
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
        return stockData.size
    }

    override fun onBindViewHolder(holder: StockListAdapter.StockViewHolder, position: Int) {
        val date = stockData[position]
        holder.setTest(date)
    }

//    override fun onBindViewHolder(
//        holder: StockViewHolder,
//        position: Int,
//        payloads: MutableList<Any>
//    ) {
//        if (payloads.isEmpty()) {
//            super.onBindViewHolder(holder, position, payloads)
//        }else {
//            for (payload in payloads) {
//                if (payload is String) {
//                    if(TextUtils.equals(payload, "following")) {
//
//                    }
//                }
//            }
//        }
//    }

    inner class StockViewHolder(val binding : ItemStockBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageViewFavorit.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mStockClickListener.stockFollowingClick(adapterPosition)
                }
                binding.imageViewFavorit.isSelected = (!binding.imageViewFavorit.isSelected)
            }
            binding.imageViewNotification.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mStockClickListener.stockAlarmClick(adapterPosition)
                }
                binding.imageViewNotification.isSelected = (!binding.imageViewNotification.isSelected)
            }
            binding.cardViewStock.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mStockClickListener.stockCardClick(adapterPosition)
                }
            }

        }

        fun setTest(stockData : StockListResponse) {
            with(binding) {
                with(stockData) {
                    textViewStockName.text = stockName

                    binding.imageViewFavorit.isSelected = isFollowing
                    binding.imageViewNotification.isSelected = isAlarm

                    if (stockExchange == "코스피") {
                        textViewMarketKinds.text = "코스피"
                        textViewMarketKinds.setBackgroundResource(R.drawable.bg_textview_kospi)
                        textViewMarketKinds.setTextColor(ContextCompat.getColor(mContext,R.color.textView_KOSPI))
                    }else{
                        textViewMarketKinds.text = "코스닥"
                        textViewMarketKinds.setBackgroundResource(R.drawable.bg_textview_kosdaq)
                        textViewMarketKinds.setTextColor(ContextCompat.getColor(mContext,R.color.textView_KOSDAQ))
                    }

                    when(stockKinds) {
                        "공모주" -> {
                            textViewStockKinds.text = "공모주"
                            textViewStockKinds.setBackgroundResource(R.drawable.bg_textview_ipo)
                            textViewStockKinds.setTextColor(ContextCompat.getColor(mContext,R.color.textView_IPO))
                        }
                        "스팩" ->  {
                            textViewStockKinds.text = "스팩주"
                            textViewStockKinds.setBackgroundResource(R.drawable.bg_textview_spac)
                            textViewStockKinds.setTextColor(ContextCompat.getColor(mContext,R.color.textView_SPAC))
                        }
                        "실권주" ->  {
                            textViewStockKinds.text = "실권주"
                            textViewStockKinds.setBackgroundResource(R.drawable.bg_textview_rightissue)
                            textViewStockKinds.setTextColor(ContextCompat.getColor(mContext,R.color.textView_rightIssue))
                        }
                        else -> {}
                    }

//                    val chipDrawable = ChipDrawable.createFromAttributes(
//                        mContext, null,0, R.style.chip_underwriter
//                    )

                    fun dpToPx (size : Float) : Float {
                        return TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, size,
                            mContext.resources.displayMetrics
                        )
                    }

                    //TODO : underwriter가 null일 수도 있으므로 대처 코드 넣기
                    //TODO : this.setChipDrawable(chipDrawable) 활용하여 한줄로 쇼부보기
                    for(name in underwriter!!.split(',')) {
                        chipGroupAlarm.addView(Chip(mContext).apply {
                                text = "${name.replace("증권","").replace("투자","").replace("금융","")}"
                                this.setEnsureMinTouchTargetSize(false)
                                this.setChipBackgroundColorResource(R.color.white)
                                this.chipStrokeWidth = dpToPx(1.5F)
                                this.setChipStrokeColorResource(R.color.chip_underwriter)
                                this.isClickable = false
                                this.chipCornerRadius = dpToPx(8F)
                                this.minHeight = 0
                                this.minWidth = 0
                                this.textEndPadding = dpToPx(2F)
                                this.textStartPadding = dpToPx(3F)
                                this.updatePadding(0, 0, 0, 0)
                                this.setTextAppearance(R.style.Chip_underwriter_TextTheme)
                        })
                    }


                    if (stockDday == 0) {
                        var day = ipoEndDate!!.split('-')
                        textViewEndDayTitle.text = "청약 마감일"
                        textViewEndDay.text = "${day[1]}월 ${day[2]}일"
                        textViewDday.text = " ${stockState} \n 진행중 "
                    }
                    else if(stockDday < 0) {
                        var day = ipoDebutDate!!.split('-')
                        textViewEndDayTitle.text = "상장일"
                        textViewEndDay.text = "${day[1]}월 ${day[2]}일"
                        textViewDday.text = " ${stockState} \n D+${(stockDday)*-1} "
                    }
                    else if(stockDday > 99) {
                        var day = ipoStartDate!!.split('-')
                        textViewEndDayTitle.text = "청약 시작일"
                        textViewEndDay.text = "${day[1]}월 ${day[2]}일"
                        textViewDday.text = " ${stockState} \n D-99 "
                    }
                    else if(stockDday >= 7) {
                        lateinit var day : List<String>
                        if (stockState == "청약") {
                            day = ipoStartDate!!.split('-')
                            textViewEndDayTitle.text = "청약 시작일"
                            textViewEndDay.text = "${day[1]}월 ${day[2]}일"
                        }
                        else if(stockState == "환불") {
                            day = ipoRefundDate!!.split('-')
                            textViewEndDayTitle.text = "환불일"
                        }
                        else {
                            day = ipoRefundDate!!.split('-')
                            textViewEndDayTitle.text = "상장일"
                        }
                        textViewEndDay.text = "${day[1]}월 ${day[2]}일"
                        textViewDday.text = " ${stockState} \n D-${stockDday} "
                        textViewDday.setBackgroundResource(R.drawable.bg_textview_nonemergency)
                    }
                    else {
                        lateinit var day : List<String>
                        if (stockState == "청약") {
                            day = ipoStartDate!!.split('-')
                            textViewEndDayTitle.text = "청약 시작일"
                            textViewEndDay.text = "${day[1]}월 ${day[2]}일"
                        }
                        else if(stockState == "환불") {
                            day = ipoRefundDate!!.split('-')
                            textViewEndDayTitle.text = "환불일"
                        }
                        else {
                            day = ipoRefundDate!!.split('-')
                            textViewEndDayTitle.text = "상장일"
                        }
                        textViewEndDay.text = "${day[1]}월 ${day[2]}일"
                        textViewDday.text = " ${stockState} \n D-${stockDday} "
                    }
                }
            }

        }
    }
}