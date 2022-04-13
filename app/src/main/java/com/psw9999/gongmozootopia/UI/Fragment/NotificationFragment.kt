package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.gongmozootopia.Adapter.CalendarViewAdapter
import com.psw9999.gongmozootopia.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    lateinit var binding : FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater,parent,false)
        binding.viewPager2Calendar.adapter = CalendarViewAdapter()
        binding.viewPager2Calendar.setCurrentItem(CalendarViewAdapter.START_POSITION, false)
        return binding.root
    }

}