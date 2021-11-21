package com.psw9999.ipo_alarm.UI.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.psw9999.ipo_alarm.R
import com.psw9999.ipo_alarm.databinding.ActivityLoadingBinding
import com.psw9999.ipo_alarm.util.NetworkStatus

class LoadingActivity : AppCompatActivity() {
    //private lateinit var mHandler: Handler
    companion object{
        // 모든 액티비티에서 로그아웃이 이루어질 수도 있기 때문에 companion object에서 선언
        lateinit var autoLoginPref : SharedPreferences
    }

    private val secondIntent by lazy {
        Intent(this, SecondActivity::class.java)
    }
    private val loginIntent by lazy {
        Intent(this, LoginActivity::class.java)
    }
    private val mainIntent by lazy {
        Intent(this, MainActivity::class.java)
    }
    private val binding by lazy {ActivityLoadingBinding.inflate(layoutInflater)}

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO : supportActionBar와 actionBar의 차이?
        //액션바 숨기기
        supportActionBar?.hide()
        //actionBar?.hide()
        setContentView(binding.root)

        // 네트워크 상태 즉시 체크
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork

        val networkStatus = NetworkStatus(this)

        // 네트워크 연결 정보를 획득하는 부분으로 미연결시 null을 반환함.
        val linkProperties = connectivityManager.getLinkProperties(currentNetwork)
        if(linkProperties != null){
            // 최초 로그인시 자동로그인인지 확인
            autoLoginPref = getPreferences(Context.MODE_PRIVATE)
            autoLoginPref?.let {
                if(it.getBoolean(getString(R.string.autoLogin), false)){
                    Thread {
                        Thread.sleep(1000)
                        val userName = it.getString("userName", "Null") as String
                        val uid = it.getString("UID","Null") as String
                        secondIntent.putExtra("userName", userName)
                        secondIntent.putExtra("UID",uid)
                        startActivity(secondIntent)
                        //TODO : 수정 필요
                        finish()
                    }.start()
                }else{
                    Thread {
                        Thread.sleep(500)
                        startActivity(mainIntent)
                        //startActivity(loginIntent)
                        //TODO : 수정 필요
                        finish()
                    }.start()
                }
            }
        }else{
            networkStatus.showNetworkDialog()
        }


        // 네트워크 상태를 체크하는 콜백 등록
        networkStatus.registerNetworkCallback()

//        with(autoLoginPref) {
//            if(getBoolean(getString(R.string.autoLogin), false)){
////                Thread {
////                    Thread.sleep(5000)
////                    startActivity(loginIntent)
////                }.start()
//            } else {
////                Thread {
////                    Thread.sleep(5000)
////                    startActivity(loginIntent)
////                }.start()
//            }
//        }
    }
}