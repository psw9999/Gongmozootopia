package com.psw9999.ipo_alarm.UI.Fragment.StockInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.ipo_alarm.R
import com.psw9999.ipo_alarm.databinding.FragmentStockInformationBinding
import com.psw9999.ipo_alarm.databinding.FragmentThirdBinding

lateinit var binding : FragmentStockInformationBinding

class CompanyInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStockInformationBinding.inflate(inflater,container,false)
        return binding.root
    }
}