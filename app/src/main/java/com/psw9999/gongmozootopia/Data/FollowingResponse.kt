package com.psw9999.gongmozootopia.Data

data class FollowingResponse(
    var stockCode : Long,
    var stockName : String,
    var stockDday : String,
    var IPO_startDay : String,
    var isFollowing : Boolean,
)
