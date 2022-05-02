package com.psw9999.gongmozootopia.UI.Fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
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
import com.psw9999.gongmozootopia.Util.AlarmReceiver
import com.psw9999.gongmozootopia.databinding.FragmentConfigurationBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.joda.time.DateTime

class ConfigurationFragment : Fragment() {

    lateinit var binding : FragmentConfigurationBinding
    lateinit var mContext: Context
    private val stockFirmViewModel : StockFirmViewModel by viewModels()

    private val alarmManager by lazy { mContext.getSystemService(ALARM_SERVICE) as AlarmManager }
    private val alarmIntent by lazy { Intent(mContext, AlarmReceiver::class.java) }
    private val pendingIntent by lazy  { PendingIntent.getBroadcast(
        mContext, AlarmReceiver.NOTIFICATION_ID, alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT) }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfigurationBinding.inflate(inflater,container,false)
        kakaoLoginCheck()
        testAlarmInit()
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

    private fun testAlarmInit() {
        binding.checkBoxBeforeIpoStartDay.setOnCheckedChangeListener { _, isChecked ->
            val toastMessage = if (isChecked) {
                val triggerTime = (SystemClock.elapsedRealtime()
                        + 10 * 1000)
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                "Onetime Alarm On"
            } else {
                alarmManager.cancel(pendingIntent)
                "Onetime Alarm Off"
            }
            Log.d("AlarmTest", toastMessage)
            Toast.makeText(mContext, toastMessage, Toast.LENGTH_SHORT).show()
        }
        binding.checkBoxIpoStartDay.setOnCheckedChangeListener { _, isChecked ->
            var testTime: Long = DateTime().plusMinutes(5).millis
            val toastMessage = if (isChecked) {
                val triggerTime = testTime
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                "5분 후 알람 시작"
            } else {
                alarmManager.cancel(pendingIntent)
                "5분 후 알람 삭제"
            }
            Log.d("AlarmTest", toastMessage)
            Toast.makeText(mContext, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }
}