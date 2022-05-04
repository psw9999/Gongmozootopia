package com.psw9999.gongmozootopia.UI.BottomSheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.psw9999.gongmozootopia.communication.RetrofitService
import com.psw9999.gongmozootopia.data.KakaoLoginStatus
import com.psw9999.gongmozootopia.databinding.LoginBottomSheetBinding
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding : LoginBottomSheetBinding

    private fun initCommunication() {
        // 통신 테스트
        val retrofit = Retrofit.Builder()
            .baseUrl("http://server.dbsg.co.kr:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service1: RetrofitService = retrofit.create(RetrofitService::class.java)
        // 추후 로그인 구현후 활성화 (Preferences -> DataStore)
//        var call: Call<LoginData> = service1.getPost(preferences.kakaoIdToken as String)
//        call.enqueue(object : Callback<LoginData> {
//            override fun onResponse(call: Call<LoginData>, response: Response<LoginData>) {
//                if (response.isSuccessful) {
//                    var result = response.body()
//                    preferences.JWS = result!!.jwt
//                    Log.d("Retrofit1", "onResponse : 성공, {${result!!.jwt}}")
//                    //initCommunication1()
//                } else {
//                    // 통신 실패한 경우 (응답코드 3xx, 4xx)
//                    Log.d("Retrofit1", "onResponse : 실패")
//                }
//            }
//            override fun onFailure(call: Call<LoginData>, t: Throwable) {
//                t.printStackTrace()
//                Log.d("Retrofit1", "onFailure : 실패")
//            }
//        })
    }

//    private fun initCommunication1() {
//        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
//
//        var gson = GsonBuilder().setLenient().create()
//
//        val retrofit : Retrofit by lazy {
//            Retrofit.Builder()
//                .baseUrl("http://server.dbsg.co.kr:8080/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(okHttpClient)
//                .build()
//        }
//
//        val service2: RetrofitService = retrofit.create(RetrofitService::class.java)
//
//        var call: Call<String> = service2.test()
//
//        call.enqueue(object : Callback<String> {
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                if (response.isSuccessful) {
//                    var result = response
//                    Log.d("Retrofit2", "onResponse : 성공, {${result.body()}}")
//                } else {
//                    // 통신 실패한 경우 (응답코드 3xx, 4xx)
//                    Log.d("Retrofit2", "onResponse : 실패")
//                }
//            }
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                t.printStackTrace()
//                Log.d("Retrofit2", "onFailure : 실패")
//            }
//        })
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.textViewLoginBottomSheetCancel.setOnClickListener {
            dismiss()
        }

        binding.imageButtonKakaoLogin.setOnClickListener{
            val loginCallback : (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("kakaoLogin", "로그인 실패", error)
                    Toast.makeText(requireContext(), "로그인 실패!", Toast.LENGTH_SHORT).show()
                }
                else if (token != null){
//                    preferences.kakaoIdToken = token.accessToken
//                    preferences.isUserLogined = true
//                    Log.d("token","${preferences.kakaoIdToken}")
                    initCommunication()
                    Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
                    EventBus.getDefault().post(KakaoLoginStatus(true))
                    dismiss()
                }
                else {
                    Toast.makeText(requireContext(), "로그인 실패!", Toast.LENGTH_SHORT).show()
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오 계정으로 로그인
            with(UserApiClient.instance) {
                if(isKakaoTalkLoginAvailable(requireContext())) {
                    loginWithKakaoTalk(requireContext(), callback = loginCallback)
                }else{
                    loginWithKakaoAccount(requireContext(), callback = loginCallback)
                }
            }
        }
    }
}
