package com.psw9999.gongmozootopia.UI.Activity

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.psw9999.gongmozootopia.Repository.FollowingListRepository
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
//                val deferredStockData = async(Dispatchers.IO) {
//                    val stockResponse = StockRepository().getStockData()
//                    stockListCheck(stockResponse)
//                }

                launch {
//                    stockData = deferredStockData.await()
//                    stockData.forEach { data ->
//                        scheduleCheck(data)
//                    }
//                    mainIntent.apply {
//                        putExtra(STOCK_DATA,stockData)
//                    }
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

    companion object {
        val STOCK_DATA : String = "STOCK_DATA"
    }
}