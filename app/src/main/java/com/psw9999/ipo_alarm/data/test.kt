package com.psw9999.ipo_alarm.data

data class test(
    var stockName : String,
    var stockCode : Long,
    var stockExchange : String,
    var stockKinds : String,
    var ipoStartDate : String?,
    var ipoEndDate : String?,
    var ipoRefundDate : String?,
    var ipoDebutDate : String?,
    var underwriter : String?,
    var tag : String?
)
