package com.psw9999.gongmozootopia.adapter

import android.content.Context
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
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.data.StockListResponse
import com.psw9999.gongmozootopia.databinding.ItemStockBinding

class StockListAdapter : RecyclerView.Adapter<StockListAdapter.StockViewHolder>(), Filterable {

    var stockList = ArrayList<StockListResponse>()
    //private var filteredList = stockList
    private var filteredList : ArrayList<StockListResponse>? = null

    lateinit var mContext : Context
    lateinit var mStockClickListener : OnStockClickListener

    fun setNewData(pos : Int, updateData : StockListResponse) {
        if (pos != RecyclerView.NO_POSITION) {
            stockList[pos] = updateData
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString : List<String> = constraint.split(',')
                var filteringList = ArrayList<StockListResponse>()
                Log.d("charString",charString[2])
                //TODO : 추후 진짜 데이터 업로드되면 그때 수정하기
//                filteringList = if (charString[0].isEmpty()) {
//                    stockList
//                } else {
//                    stockList
//                }
//
                if (charString[1].isEmpty()) {
                    filteringList = stockList
                } else {
                    val filteringWord = charString[1]
                    for (item in stockList) {
                        if(filteringWord == item.stockKinds) filteringList.add(item)
                    }
                }

                if (charString[2] == "true") {
                    var temp = ArrayList<StockListResponse>()
                    for (item in filteringList) {
                        if(item.isFollowing) temp.add(item)
                    }
                    filteringList = temp
                }

                val filterResults = FilterResults()
                filterResults.values = filteringList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<StockListResponse>
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
        return if (filteredList == null) {
            stockList.size
        }else{
            filteredList!!.size
        }
    }

    override fun onBindViewHolder(holder: StockListAdapter.StockViewHolder, position: Int) {
        if (filteredList == null) {
            //val date = stockList[position]
            holder.setTest(stockList[position])
        }else{
            holder.setTest(filteredList!![position])
        }
        //val date = stockList[position]
    }

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


                    fun dpToPx (size : Float) : Float {
                        return TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, size,
                            mContext.resources.displayMetrics
                        )
                    }
                    chipGroupAlarm.removeAllViews()
                    //TODO : this.setChipDrawable(chipDrawable) 활용하여 한줄로 쇼부보기
                    underwriter?.let {
                        for(name in it.split(',')) {
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
                    }
                }
            }
        }

    }
}
