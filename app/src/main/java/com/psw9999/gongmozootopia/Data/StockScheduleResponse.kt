package com.psw9999.gongmozootopia.Data

data class StockScheduleResponse(
    val ipoIndex : Long,
    val stockName : String,
    var ipoForecastDate : String?,
    var ipoStartDate : String?,
    var ipoEndDate : String?,
    var ipoRefundDate : String?,
    var ipoDebutDate : String?
)
