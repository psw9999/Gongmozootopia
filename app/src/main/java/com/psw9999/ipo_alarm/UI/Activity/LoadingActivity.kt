package com.psw9999.ipo_alarm.UI.Activity

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.common.KakaoSdk
import com.psw9999.ipo_alarm.Repository.AccountRepository
import com.psw9999.ipo_alarm.base.BaseApplication.Companion.helper
import com.psw9999.ipo_alarm.base.BaseApplication.Companion.stockListKey
import com.psw9999.ipo_alarm.communication.ServerImpl
import com.psw9999.ipo_alarm.data.StockListResponse
import com.psw9999.ipo_alarm.databinding.ActivityLoadingBinding
import com.psw9999.ipo_alarm.util.NetworkStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadingActivity : AppCompatActivity() {

    private val mainIntent by lazy {
        Intent(this, MainActivity::class.java)
    }
    //test
    private val infoIntent by lazy {
        Intent(this, StockInformationActivity::class.java)
    }
    private val binding by lazy {ActivityLoadingBinding.inflate(layoutInflater)}

    lateinit var stockListResponse: ArrayList<StockListResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO : supportActionBar와 actionBar의 차이?
        //액션바 숨기기
        supportActionBar?.hide()
        setContentView(binding.root)
        // 카카오 로그인 SDK 초기화
        KakaoSdk.init(this, "f23fe6f5f5fc7a04094619143ffa9832")
        //helper = sqliteHelper(this,"stockDB",2)
        testMode()
        // 네트워크 상태 즉시 체크
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val networkStatus = NetworkStatus(this)

        // 네트워크 연결 정보를 획득하는 부분으로 미연결시 null을 반환함.
        val linkProperties = connectivityManager.getLinkProperties(currentNetwork)
        if(linkProperties != null){
            stockListResponse = ArrayList<StockListResponse>()
            CoroutineScope(Dispatchers.IO).launch() {
                launch{
                    stockListResponse = AccountRepository().getStockList()
                    Log.d("deferred1","OK")
                    true
                }.join()
                launch {
                    stockListResponse = helper.selectSQLiteDB(stockListResponse)
                    mainIntent.apply {
                        putParcelableArrayListExtra(stockListKey,stockListResponse)
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

    //TODO : 메서드명 변경하기
    private fun receiveStockList() {
        var call: Call<ArrayList<StockListResponse>> = ServerImpl.APIService.getStockList()
        call.enqueue(object : Callback<ArrayList<StockListResponse>> {
            override fun onResponse(call: Call<ArrayList<StockListResponse>>, response: Response<ArrayList<StockListResponse>>) {
                if (response.isSuccessful) {
                    val result : ArrayList<StockListResponse>? = response.body()
                    Log.d("receiveStockList","OK")
//                    if (result != null) {
//                        for (stockData in result) {
//                            helper.insertSQLiteDB(stockData)
//                        }
//                    }
                    if (result != null) {
                        helper.selectSQLiteDB(result)
                    }
                    mainIntent.apply {
                        putParcelableArrayListExtra(stockListKey,result)
                    }
//                    startActivity(mainIntent)
//                    finish()
                } else {
                    // 통신 실패한 경우 (응답코드 3xx, 4xx)
                    Log.d("Retrofit1", "onResponse : 실패")
                }
            }
            override fun onFailure(call: Call<ArrayList<StockListResponse>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@LoadingActivity,"서버와 통신 실패",Toast.LENGTH_LONG).show()
                Log.d("Retrofit1", "onFailure : 실패")
            }
        })
    }

    private fun testMode() {
        binding.textViewLoadingText.setOnClickListener {
//            val testList : ArrayList<StockData> = arrayListOf()
//            testList.add(StockData(
//                ipoIndex = 20,
//                stockName = "테스트테스트",
//                stockExchange = "코스닥",
//                stockKinds = "공모주",
//                ipoStartDate = "2022-02-18",
//                ipoEndDate = "2022-02-20",
//                ipoRefundDate = "2022-02-22",
//                ipoDebutDate = "2022-02-24",
//                underwriter = "KB증권,삼성증권,미래에셋증권,카카오증권",
//                tag = null,
//                isFollowing = false,
//                stockDday = 5,
//                stockState = "청약",
//                isAlarm = false))
//            mainIntent.apply {
//                putParcelableArrayListExtra(stockListKey,testList)
//            }
//            startActivity(mainIntent)
            startActivity(infoIntent)
            finish()
        }
    }
}