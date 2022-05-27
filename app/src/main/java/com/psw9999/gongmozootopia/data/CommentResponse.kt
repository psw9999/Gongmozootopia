package com.psw9999.gongmozootopia.data

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    @SerializedName("commentIndex") val commentIndex : Long,
    @SerializedName("ipoIndex") val ipoIndex : Long,
    @SerializedName("writer") val writer : String,
    @SerializedName("stockName") val stockName : String,
    @SerializedName("comment") val comment : String,
    @SerializedName("registDate") var registDate : String,
)