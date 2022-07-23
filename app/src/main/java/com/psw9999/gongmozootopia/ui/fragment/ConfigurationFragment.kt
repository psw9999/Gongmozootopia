package com.psw9999.gongmozootopia.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.psw9999.gongmozootopia.databinding.FragmentConfigurationBinding
import com.psw9999.gongmozootopia.viewModel.ConfigurationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ConfigurationFragment : Fragment() {

    lateinit var binding : FragmentConfigurationBinding
    lateinit var mContext: Context
    private val configurationViewModel : ConfigurationViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfigurationBinding.inflate(inflater,container,false)
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

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}