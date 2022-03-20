package com.psw9999.gongmozootopia.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockListResponse(
    var ipoIndex : Long,
    var stockName : String,
    var stockExchange : String,
    var stockKinds : String,
    var ipoStartDate : String,
    var ipoEndDate : String?,
    var ipoRefundDate : String?,
    var ipoDebutDate : String?,
    var underwriter : String?,
    var tag : String?,
    var stockState : String?,
    var stockDday : Int,
    var isFollowing : Boolean = false,
    var isAlarm : Boolean = false
) : Parcelable
