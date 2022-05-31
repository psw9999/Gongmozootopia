package com.psw9999.gongmozootopia.data

data class StockInfoResponse(
    val ipoIndex: Long,
    val stockName: String,
    val stockExchange: String,
    val stockKinds: String,
    var sector : String?,
    val profits: Long,
    val sales: Long,
    var exStartDate : String?,
    var exEndDate : String?,
    var ipoForecastStart: String?,
    var ipoForecastEnd: String?,
    var ipoStartDate: String?,
    var ipoEndDate: String?,
    var ipoRefundDate: String?,
    var ipoDebutDate: String?,
    val lockUpPercent: Float?,
    val ipoInstitutionalAcceptanceRate: Float?,
    val ipoPrice: Long?,
    val ipoPriceLow: Long?,
    val ipoPriceHigh: Long?,
    val ipoMinDeposit: Long?,
    val underwriter: String?,
    val putBackOptionWho: String?,
    val putBackOptionPrice: Long?,
    val putBackOptionDeadline: String?,
    var isFollowing : Boolean = false
)

