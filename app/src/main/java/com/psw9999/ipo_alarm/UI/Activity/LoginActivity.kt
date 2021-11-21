package com.psw9999.ipo_alarm.UI.Activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.psw9999.ipo_alarm.R
import com.psw9999.ipo_alarm.UI.Activity.LoadingActivity.Companion.autoLoginPref
import com.psw9999.ipo_alarm.databinding.ActivityLoginBinding
import com.psw9999.ipo_alarm.util.NetworkStatus


class LoginActivity : AppCompatActivity() {

    val TAG : String = "LoginActivity"

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var kakaoApiClient: UserApiClient

    private val secondIntent by lazy { Intent(this, SecondActivity::class.java)  }

    private fun kakaoLogin() {
        kakaoApiClient.loginWithKakaoAccount(baseContext) { token, error ->
            binding.run {
                if (error != null) {
                    Log.e(TAG, "로그인 실패", error)
                    textViewGuideText.text = "로그인 실패"
                } else if (token != null) {
                    Log.i(TAG, "로그인 성공 ${token.accessToken}")
                    kakaoApiClient.me { user, error ->
                        if (error != null) {
                            Log.e(TAG, "사용자 정보 요청 실패", error)
                            textViewGuideText.text = "사용자 정보 요청 실패"
                        } else if (user != null) {
                            // 자동로그인이 체크된 경우
                            if (checkBoxAutoLogin.isChecked) {
                                with(autoLoginPref.edit()) {
                                    putBoolean(getString(R.string.autoLogin), true)
                                    putString("UID","$token")
                                    putString("userName","${user.kakaoAccount?.profile?.nickname}")
                                    apply()
                                }
                            }
                            secondIntent.putExtra("userName","${user.kakaoAccount?.profile?.nickname}")
                            startActivity(secondIntent)
                            finish()
                        }
                    }
                }
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val networkStatus = NetworkStatus(this)
        networkStatus.registerNetworkCallback()

        // 카카오 API 호출을 위한 디버그, 릴리즈 키 해시값 확인
        // 초기 한번만 등록하면 되므로, 비활성화
        //var keyHash = Utility.getKeyHash(this)
        //Log.d("keyHash",keyHash)

        // 카카오 SDK 초기화 (git 업로드시 apiKey 지우기)
        KakaoSdk.init(this, "f23fe6f5f5fc7a04094619143ffa9832")
        kakaoApiClient = UserApiClient()

        //  카카오톡이 스마트폰 내에 설치되었는지 여부 체크
        if (kakaoApiClient.isKakaoTalkLoginAvailable(baseContext)) {
            binding.run{
                textViewGuideText.text = "투자 판단에 대한 모든 책임은 투자자 본인에게 있습니다."
                imageButtonKakaoLogin.setOnClickListener {
                    kakaoLogin()
                }
            }
        }else{
            binding.run {
                textViewGuideText.text = "카카오톡이 설치되어 있지 않아요."
                imageButtonKakaoLogin.visibility = View.GONE
            }
        }
    }
}