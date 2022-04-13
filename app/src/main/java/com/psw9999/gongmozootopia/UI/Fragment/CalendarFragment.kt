package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.gongmozootopia.Adapter.CalendarAdapter
import com.psw9999.gongmozootopia.databinding.FragmentCalendarBinding


class CalendarFragment : Fragment() {

    private lateinit var calendarAdapter : CalendarAdapter
    public lateinit var binding : FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater,container,false)
        //initCalendarAdapter()
        return binding.root
    }

}