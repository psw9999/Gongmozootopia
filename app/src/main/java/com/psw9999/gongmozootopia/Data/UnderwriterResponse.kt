package com.psw9999.gongmozootopia.Data

data class UnderwriterResponse(
    val ipoIndex : Long,
    val updateDate : String,
    val underName : String,
    val indTotalMax : Long?,
    val indTotalMin : Long?,
    val indCanMax : Long?,
    val indCanMin : Long?
)
