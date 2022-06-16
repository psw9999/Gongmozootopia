package com.psw9999.gongmozootopia.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockResponse(
    @SerializedName("ipoIndex") val ipoIndex : Long,
    @SerializedName("stockName") val stockName : String,
    @SerializedName("stockExchange") var stockExchange : String,
    @SerializedName("stockKinds") var stockKinds : String?,
    @SerializedName("ipoForecastEnd") var ipoForecastDate : String?,
    @SerializedName("ipoStartDate") var ipoStartDate : String?,
    @SerializedName("ipoEndDate") var ipoEndDate : String?,
    @SerializedName("ipoRefundDate") var ipoRefundDate : String?,
    @SerializedName("ipoDebutDate") var ipoDebutDate : String?,
    @SerializedName("underwriter") var underwriter : String?,
    var isFollowing : Boolean = false,
    var currentSchedule : String?,
    var scheduleDday : String?
) : Parcelable
