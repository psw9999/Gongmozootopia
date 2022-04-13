package com.psw9999.gongmozootopia.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_following")
data class StockFollowingResponse(
    @PrimaryKey val ipoIndex : Long,
    @ColumnInfo(name = "stock_name") val stockName: String,
    @ColumnInfo(name = "is_following") var isFollowing : Boolean = false
)
