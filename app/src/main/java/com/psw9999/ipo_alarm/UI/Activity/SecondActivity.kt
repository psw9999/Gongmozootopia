package com.psw9999.ipo_alarm.UI.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.psw9999.ipo_alarm.UI.Activity.LoadingActivity.Companion.autoLoginPref
import com.psw9999.ipo_alarm.databinding.ActivitySecondBinding
import com.psw9999.ipo_alarm.util.NetworkStatus

class SecondActivity : AppCompatActivity() {
    val binding by lazy {ActivitySecondBinding.inflate(layoutInflater)}


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val networkStatus = NetworkStatus(this)
        networkStatus.registerNetworkCallback()

        binding.run {
            textViewAutoLoginTest.text = "${intent.getStringExtra("userName")}님 안녕하세요?"
            buttonLogOut.setOnClickListener {
                with(autoLoginPref.edit()){
                    // 자동로그인 관련 데이터 모두 삭제 후 저장
                    clear()
                    apply()
                }
                val loginIntent = Intent(baseContext, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            }
        }

    }
}