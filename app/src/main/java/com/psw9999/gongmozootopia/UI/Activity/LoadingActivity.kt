package com.psw9999.gongmozootopia.UI.Activity

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kakao.sdk.common.KakaoSdk
import com.psw9999.gongmozootopia.Repository.StockRepository
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.databinding.ActivityLoadingBinding
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.today
import com.psw9999.gongmozootopia.Util.NetworkStatus
import kotlinx.coroutines.*
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.time.LocalDate

class LoadingActivity : AppCompatActivity() {

    private val mainIntent by lazy { Intent(this, MainActivity::class.java) }
    lateinit var stockData : ArrayList<StockResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 카카오 로그인 SDK 초기화
        // KakaoSdk.init(this, "f23fe6f5f5fc7a04094619143ffa9832")

        // 네트워크 상태 즉시 체크
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val networkStatus = NetworkStatus(this)

        // 네트워크 연결 정보를 획득하는 부분으로 미연결시 null을 반환함.
        val linkProperties = connectivityManager.getLinkProperties(currentNetwork)
        if(linkProperties != null){
            CoroutineScope(Dispatchers.Default).launch {
                // 1. stockList 수신
                val deferredStockData = async(Dispatchers.IO) {
                    val stockResponse = StockRepository().getStockData()
                    stockDataFiltering(stockResponse)
                }

                launch {
                    stockData = deferredStockData.await()
                    stockData.forEach { data ->
                        scheduleCheck(data)
                    }
                    mainIntent.apply {
                        putExtra(STOCK_DATA,stockData)
                    }
                    startActivity(mainIntent)
                    finish()
                }
            }
        }else{
            networkStatus.showNetworkDialog()
        }
        // 네트워크 상태를 체크하는 콜백 등록
        networkStatus.registerNetworkCallback()
    }

    private suspend fun stockDataFiltering(stockData : ArrayList<StockResponse>) : ArrayList<StockResponse> {
        return arrayListOf<StockResponse>().apply {
            for (data in stockData) {
                if (data.stockKinds == null || data.ipoIndex == null || data.stockExchange == null) continue

                var index = 0
                try {
                    val timeDate = arrayOf(
                        data.ipoStartDate,
                        data.ipoEndDate,
                        data.ipoDebutDate,
                        data.ipoRefundDate
                    )
                    for (i in timeDate.indices) {
                        LocalDate.parse(timeDate[i])
                        index++
                    }
                }catch(e : Exception){
                    if (index == 0 || index == 1) {
                        continue
                    }
                    else if (index == 2) {
                        data.ipoDebutDate = null
                        data.ipoRefundDate = null
                    }
                    else {
                        data.ipoDebutDate = null
                    }
                }

                data.underwriter?.let{ underwriter ->
                    val words = Regex("증권|투자|금융")
                    var sb = StringBuilder()
                    underwriter.split(",").forEach { temp ->
                        if (temp.contains(words)) {
                            sb.append(temp.replace("증권","").replace("투자","").replace("금융","").replace("㈜","").replace("(주)","").plus(","))
                        }
                    }
                    if (sb.isEmpty()) data.underwriter = null
                    else data.underwriter = sb.substring(0, sb.length-1)
                }
                this.add(data)
            }
        }
    }

    private fun scheduleCheck(stockData : StockResponse){
        var fmt : DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        val ipoStartDday : Int =
            stockData.ipoStartDate?.let {Days.daysBetween(today, fmt.parseDateTime(stockData.ipoStartDate)).days}?:-1
        val ipoEndDday : Int =
            stockData.ipoEndDate?.let {Days.daysBetween(today, fmt.parseDateTime(stockData.ipoEndDate)).days}?:-1
        val refundDday : Int =
            stockData.ipoRefundDate?.let{Days.daysBetween(today, fmt.parseDateTime(stockData.ipoRefundDate)).days}?:-1
        val debutDday : Int =
            stockData.ipoDebutDate?.let {Days.daysBetween(today, fmt.parseDateTime(stockData.ipoDebutDate)).days}?:-1

        if (ipoStartDday == -1) {
            stockData.currentSchedule = "청약시작일 미정"
            stockData.scheduleDday = ""
        }
        else if (ipoStartDday > 0) {
            stockData.currentSchedule = "청약시작일 ${stockData.ipoStartDate!!.slice(5..6)}월 ${stockData.ipoStartDate!!.slice(8..9)}일"
            stockData.scheduleDday = "D-$ipoStartDday"
        }
        else if (ipoEndDday >= 0) {
            stockData.currentSchedule = "청약마감일 ${stockData.ipoEndDate!!.slice(5..6)}월 ${stockData.ipoEndDate!!.slice(8..9)}일"
            stockData.scheduleDday = "진행중"
        }
        else if (refundDday >= 0) {
            stockData.currentSchedule = "환불일 ${stockData.ipoRefundDate!!.slice(5..6)}월 ${stockData.ipoRefundDate!!.slice(8..9)}일"
            if (refundDday == 0) stockData.scheduleDday = "D-day"
            else stockData.scheduleDday = "D-$refundDday"
        }
        else if (debutDday >= 0) {
            stockData.currentSchedule = "상장일 ${stockData.ipoDebutDate!!.slice(5..6)}월 ${stockData.ipoDebutDate!!.slice(8..9)}일"
            if (refundDday == 0) stockData.scheduleDday = "D-day"
            else stockData.scheduleDday = "D-$debutDday"
        }
        else {
            stockData.currentSchedule = "상장완료"
            stockData.scheduleDday = ""
        }
    }

    companion object {
        val STOCK_DATA : String = "STOCK_DATA"
    }
}