package com.psw9999.gongmozootopia.ui.Fragment

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
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.psw9999.gongmozootopia.viewModel.ConfigurationViewModel
import com.psw9999.gongmozootopia.Util.AlarmReceiver
import com.psw9999.gongmozootopia.databinding.FragmentConfigurationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class ConfigurationFragment : Fragment() {

    lateinit var binding : FragmentConfigurationBinding
    lateinit var mContext: Context
    private val configurationViewModel : ConfigurationViewModel by viewModels()

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
        testAlarmInit()
        initConfiguration()
        return binding.root
    }

    private fun initConfiguration() {
        configurationViewModel.stockFirmMap.observeOnce(viewLifecycleOwner, Observer {
            binding.chipGroupStockFirm.children.forEach { chip ->
                (chip as Chip).isChecked = it.getOrDefault(chip.tag, false)
            }
        })

        CoroutineScope(Dispatchers.Main).launch {
            binding.switchForfeitedStockFiltering.isChecked = configurationViewModel.forfeitedFlow.first()
            binding.switchSpacStockFiltering.isChecked = configurationViewModel.spacFlow.first()
        }

        binding.chipGroupStockFirm.children.forEach { chip ->
            (chip as Chip).setOnClickListener {
                configurationViewModel.setStockFirmData(chip.tag!! as String, chip.isChecked)
            }
        }
        with(binding.switchSpacStockFiltering) {
            setOnCheckedChangeListener { _, isChecked ->
                configurationViewModel.setSpacEnabled(isChecked)
            }
        }
        with(binding.switchForfeitedStockFiltering) {
            setOnCheckedChangeListener { _, isChecked ->
                configurationViewModel.setForfeitedEnabled(isChecked)
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

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}