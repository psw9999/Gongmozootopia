package com.psw9999.gongmozootopia.data

import com.google.gson.annotations.SerializedName

data class StockInfoResponse(
        @SerializedName("ipo")
    val stockInfo : StockInfo
)

data class StockInfo(
        val ipoIndex: Long,
        val stockName: String,
        val stockExchange: String,
        val stockKinds: String,
        val stockCode: Long,
        //val dartCode : Long,
        //val sectorCode : Long,
        val profits: Long?,
        val sales: Long?,
        val ipoForecastDate: String?,
        val ipoStartDate: String?,
        val ipoEndDate: String?,
        val ipoRefundDate: String?,
        val ipoDebutDate: String?,
        val lockUpPercent: Long,
        val ipoInstitutionalAcceptanceRate: Long,
        val ipoPrice: Long?,
        val ipoPriceLow: Long?,
        val ipoPriceHigh: Long?,
        val ipoMinDeposit: Long?,
        val underwriter: String?,
        val putBackOptionWho: String?,
        val putBackOptionPrice: Long?,
        val putBackOptionDeadline: String?,
        var isFollowing : Boolean = false,
        var isAlarm : Boolean = false
        //val tag: String?
    )

