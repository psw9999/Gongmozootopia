package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kakao.sdk.user.UserApiClient
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.preferences
import com.psw9999.gongmozootopia.UI.BottomSheet.LoginBottomSheet
import com.psw9999.gongmozootopia.data.KakaoLoginStatus
import com.psw9999.gongmozootopia.databinding.FragmentThirdBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ThirdFragment : Fragment() {

    lateinit var binding : FragmentThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(inflater,container,false)
        kakaoLoginCheck()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.buttonLoginLogout.setOnClickListener {
            if(preferences.isUserLogined) {
                kakaoLogout()
            }else{
                val bottomSheet = LoginBottomSheet()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onKakaoLoginDoneEvent(kakaoLoginStatus : KakaoLoginStatus) {
        if(kakaoLoginStatus.isKakaoLogined){
            binding.imageViewLoginLogout.setImageResource(R.drawable.logout)
            binding.textViewLoginLogout.text = "로그아웃"
        }else{
            binding.imageViewLoginLogout.setImageResource((R.drawable.login))
            binding.textViewLoginLogout.text = "로그인"
        }
    }

    private fun kakaoLoginCheck() {
        if(preferences.isUserLogined) {
            binding.imageViewLoginLogout.setImageResource(R.drawable.logout)
            binding.textViewLoginLogout.text = "로그아웃"
        }else{
            binding.imageViewLoginLogout.setImageResource(R.drawable.login)
            binding.textViewLoginLogout.text = "로그인"
        }
    }

    private fun kakaoLogout() {
        EventBus.getDefault().post(KakaoLoginStatus(false))
        preferences.clearPreferences()
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("logout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            }
            else {
                Log.i("logout", "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }
}