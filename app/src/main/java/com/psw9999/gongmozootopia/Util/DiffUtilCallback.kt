package com.psw9999.gongmozootopia.Util

import androidx.recyclerview.widget.DiffUtil
import com.psw9999.gongmozootopia.data.StockResponse

class DiffUtilCallback(private val originalList : List<Any>, private val filteredList : List<Any>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = originalList.size

    override fun getNewListSize(): Int = filteredList.size

    // 두 아이템이 같은 객체인지 여부를 판단
    override fun areItemsTheSame(originalItemPosition: Int, filteredItemPosition: Int): Boolean {
        val originalItem = originalList[originalItemPosition]
        val filteredItem = filteredList[filteredItemPosition]

        return (originalItem is StockResponse) && (filteredItem is StockResponse)
    }

    override fun areContentsTheSame(originalItemPosition: Int, filteredItemPosition: Int): Boolean =
        originalList[originalItemPosition] == filteredList[filteredItemPosition]
}