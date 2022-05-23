package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.psw9999.gongmozootopia.Adapter.CalendarAdapter
import com.psw9999.gongmozootopia.Adapter.CalendarViewAdapter
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.today
import com.psw9999.gongmozootopia.viewModel.ScheduleViewModel
import com.psw9999.gongmozootopia.databinding.FragmentCalendarBinding
import com.psw9999.gongmozootopia.databinding.FragmentMainBinding
import com.psw9999.gongmozootopia.databinding.FragmentTempBinding
import org.joda.time.DateTime

class TempFragment : Fragment() {
    lateinit var binding : FragmentTempBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTempBinding.inflate(inflater, container, false)
        return binding.root
    }
}