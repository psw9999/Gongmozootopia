package com.psw9999.gongmozootopia.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockResponse(
    val ipoIndex : Long,
    val stockName : String,
    var stockExchange : String,
    var stockKinds : String,
    var ipoStartDate : String,
    var ipoEndDate : String?,
    var ipoRefundDate : String?,
    var ipoDebutDate : String?,
    var underwriter : String?,
    var isFollowing : Boolean = false,
    var currentSchedule : String?,
    var scheduleDday : String?
) : Parcelable
