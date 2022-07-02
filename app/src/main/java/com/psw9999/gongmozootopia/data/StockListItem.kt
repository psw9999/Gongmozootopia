package com.psw9999.gongmozootopia.data

sealed class StockListItem {
    data class StockItem(val stock: StockResponse) : StockListItem()
    data class SeparatorItem(val headerText: String) : StockListItem()
}
