package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.adapter.CalendarAdapter
import com.psw9999.gongmozootopia.adapter.CalendarViewAdapter
import com.psw9999.gongmozootopia.databinding.FragmentNotificationBinding
import com.psw9999.gongmozootopia.databinding.HolderCalendarBinding

class NotificationFragment : Fragment() {

    lateinit var binding : FragmentNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding = FragmentNotificationBinding.inflate(inflater,parent,false)
        //binding.recyclerViewCalendar.adapter = CalendarViewAdapter()
        //return binding.root
        return HolderCalendarBinding.inflate(inflater,parent,false).root
    }

}