package com.psw9999.gongmozootopia.UI.Fragment.StockInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.gongmozootopia.databinding.FragmentStockInformationBinding


class CompanyInfoFragment : Fragment() {

    lateinit var binding : FragmentStockInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStockInformationBinding.inflate(inflater,container,false)
        return binding.root
    }
}