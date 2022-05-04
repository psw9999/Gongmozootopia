package com.psw9999.gongmozootopia.data

data class StockFirmResponse(val map : Map<String,Boolean>) {
    val Mirae : Boolean by map
    val Hankook : Boolean by map
    val Kiwoom : Boolean by map
    val NH : Boolean by map
    val Samsung : Boolean by map
    val Meritz: Boolean by map
    val Hana: Boolean by map
    val KB : Boolean by map
    val Daishin: Boolean by map
    val Shinyoung : Boolean by map
    val Shinhan : Boolean by map
    val IBK: Boolean by map
    val DB : Boolean by map
    val Hanhwa: Boolean by map
}
