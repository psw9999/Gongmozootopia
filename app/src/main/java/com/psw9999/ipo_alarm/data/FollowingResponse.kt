package com.psw9999.ipo_alarm.data

data class FollowingResponse(
    var stockCode : Long,
    var stockName : String,
    var stockDday : String,
    var IPO_startDay : String,
    var isFollowing : Boolean,
)
