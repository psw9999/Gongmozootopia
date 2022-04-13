package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.kakao.sdk.user.UserApiClient
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.preferences
import com.psw9999.gongmozootopia.UI.BottomSheet.LoginBottomSheet
import com.psw9999.gongmozootopia.ViewModel.StockFirmViewModel
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.settingsManager
import com.psw9999.gongmozootopia.Data.KakaoLoginStatus
import com.psw9999.gongmozootopia.databinding.FragmentConfigurationBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ConfigurationFragment : Fragment() {

    lateinit var binding : FragmentConfigurationBinding
    private val stockFirmViewModel : StockFirmViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfigurationBinding.inflate(inflater,container,false)
        kakaoLoginCheck()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initChip()
        stockFirmViewModel.stockFirmData.observe(viewLifecycleOwner, Observer {
                Log.d("it","$it")
                binding.chipGroupStockFirm.children.forEach { chip ->
                    (chip as Chip).isChecked = it[chip.tag]!!
                }
        })

        binding.buttonLoginLogout.setOnClickListener {
            if(preferences.isUserLogined) {
                kakaoLogout()
            }else{
                val bottomSheet = LoginBottomSheet()
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            }
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

    private fun initChip() {
        binding.chipGroupStockFirm.children.forEach { chip ->
            (chip as Chip).setOnClickListener {
                GlobalScope.launch {
                    settingsManager.setStockFirmEnable(chip.tag!! as String, chip.isChecked)
                }
            }
        }
    }
}