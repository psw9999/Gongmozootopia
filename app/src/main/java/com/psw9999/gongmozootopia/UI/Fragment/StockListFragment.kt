package com.psw9999.gongmozootopia.UI.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.psw9999.gongmozootopia.UI.Activity.StockInformationActivity
import com.psw9999.gongmozootopia.Util.CalendarUtils.Companion.today
import com.psw9999.gongmozootopia.viewModel.ConfigurationViewModel
import com.psw9999.gongmozootopia.databinding.FragmentStockListBinding
import com.psw9999.gongmozootopia.Util.GridViewDecoration
import com.psw9999.gongmozootopia.adapter.StockListPagingAdapter
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.dpToPx
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockListItem
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.viewModel.StockListViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter

class StockListFragment : Fragment() {
    private lateinit var binding : FragmentStockListBinding
    private lateinit var stockListAdapter : StockListPagingAdapter

    private val stockListViewModel : StockListViewModel by viewModels()
    private val configurationViewModel : ConfigurationViewModel by viewModels()

    private val stockInfoIntent by lazy {
        Intent(requireContext(), StockInformationActivity::class.java)
    }

    private val TAG = "StockListFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStockListBinding.inflate(inflater,container,false)
        Log.d("today","$today")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stockListViewModel.stockList.combine(configurationViewModel.kindFilterFlow) { stockList, kindFiltering ->
                    stockList.filter { stockListItem ->
                        when (stockListItem) {
                            is StockListItem.StockItem -> {
                                stockListItem.stock.stockKinds in kindFiltering
                            }
                            else -> {
                                true
                            }
                        }
                    }
                }.collectLatest {
                    stockListAdapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stockListAdapter.loadStateFlow.collectLatest {
                    binding.appendProgress.isVisible = it.append is LoadState.Loading
                }
            }
        }

        // TODO : 해당 옵저버 추가시 에뮬레이터에서 Skip frame 워닝 뜸.
        stockListViewModel.followingList.observe(viewLifecycleOwner, Observer { followingList ->
            stockListAdapter.setFollowingList(followingList)
        })

        configurationViewModel.stockFirmMap.observe(viewLifecycleOwner, Observer { stockFirmMap ->
            stockListAdapter.setStockFirmMap(stockFirmMap)
        })

    }

    private fun onClickSetting() {
        stockListAdapter.setOnStockClickListener(object : StockListPagingAdapter.OnStockClickListener {
            override fun stockCardClick(ipoIndex: Long) {
                stockInfoIntent.apply {
                    putExtra("ipoIndex", ipoIndex)
                }
                startActivity(stockInfoIntent)
            }

            override fun stockFollowingClick(followingResponse: FollowingResponse) {
                if (!followingResponse.isFollowing) {
                    stockListViewModel.addFollowing(followingResponse)
                    Snackbar.make(view!!, "${followingResponse.stockName}의 팔로잉을 설정하였습니다.",Snackbar.LENGTH_SHORT).show()
                }else{
                    stockListViewModel.deleteFollowing(followingResponse.ipoIndex)
                    Snackbar.make(view!!, "${followingResponse.stockName}의 팔로잉을 해제하였습니다.",Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initRecyclerView() {
        stockListAdapter = StockListPagingAdapter()
        binding.recyclerViewStockList.adapter = stockListAdapter
        binding.recyclerViewStockList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStockList.addItemDecoration(GridViewDecoration(dpToPx(requireContext(),10F).toInt()))
        onClickSetting()
    }
}