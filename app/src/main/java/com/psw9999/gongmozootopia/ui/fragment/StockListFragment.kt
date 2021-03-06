package com.psw9999.gongmozootopia.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.psw9999.gongmozootopia.Util.GridViewDecoration
import com.psw9999.gongmozootopia.adapter.StockListPagingAdapter
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.dpToPx
import com.psw9999.gongmozootopia.base.BaseFragment
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockListItem
import com.psw9999.gongmozootopia.databinding.FragmentStockListBinding
import com.psw9999.gongmozootopia.paging.StockScheduleQuery
import com.psw9999.gongmozootopia.ui.activity.StockInformationActivity
import com.psw9999.gongmozootopia.viewModel.ConfigurationViewModel
import com.psw9999.gongmozootopia.viewModel.StockListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StockListFragment : BaseFragment<FragmentStockListBinding>(FragmentStockListBinding::inflate) {
    private lateinit var stockListAdapter: StockListPagingAdapter
    private val stockListViewModel: StockListViewModel by viewModels()
    private val configurationViewModel: ConfigurationViewModel by viewModels()

    private val stockInfoIntent by lazy {
        Intent(requireContext(), StockInformationActivity::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stockListViewModel.stockList.combine(configurationViewModel.kindFilterFlow) { stockList, kindFiltering ->
                    stockList
                        .filter { stockListItem ->
                            when (stockListItem) {
                                is StockListItem.StockItem -> {
                                    stockListItem.stock.stockKinds in kindFiltering
                                }
                                else -> {
                                    true
                                }
                            }
                        }
                        .insertSeparators { before, after ->
                            if (before is StockListItem.SeparatorItem && after is StockListItem.SeparatorItem) {
                                when (before.headerText) {
                                    StockScheduleQuery.TodaySchedule.title -> StockListItem.EmptyItem(
                                        StockScheduleQuery.TodaySchedule.emptyGuide
                                    )
                                    StockScheduleQuery.IpoExpectedSchedule.title -> StockListItem.EmptyItem(
                                        StockScheduleQuery.IpoExpectedSchedule.emptyGuide
                                    )
                                    StockScheduleQuery.RefundExpectedSchedule.title -> StockListItem.EmptyItem(
                                        StockScheduleQuery.RefundExpectedSchedule.emptyGuide
                                    )
                                    StockScheduleQuery.DebutExpectedSchedule.title -> StockListItem.EmptyItem(
                                        StockScheduleQuery.DebutExpectedSchedule.emptyGuide
                                    )
                                    else -> null
                                }
                            } else {
                                null
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
                    binding.loadingProgress.isVisible = it.source.refresh is LoadState.Loading
                    binding.appendProgress.isVisible = it.append is LoadState.Loading
                }
            }
        }

        // TODO : ?????? ????????? ????????? ????????????????????? Skip frame ?????? ???.
        stockListViewModel.followingList.observe(viewLifecycleOwner) { followingList ->
            stockListAdapter.setFollowingList(followingList)
        }

        configurationViewModel.stockFirmMap.observe(viewLifecycleOwner) { stockFirmMap ->
            stockListAdapter.setStockFirmMap(stockFirmMap)
        }
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
                    Snackbar.make(
                        view!!,
                        "${followingResponse.stockName}??? ???????????? ?????????????????????.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    stockListViewModel.deleteFollowing(followingResponse.ipoIndex)
                    Snackbar.make(
                        view!!,
                        "${followingResponse.stockName}??? ???????????? ?????????????????????.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun initRecyclerView() {
        stockListAdapter = StockListPagingAdapter()
        binding.recyclerViewStockList.adapter = stockListAdapter
        binding.recyclerViewStockList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStockList.addItemDecoration(
            GridViewDecoration(dpToPx(requireContext(), 10F).toInt())
        )
        onClickSetting()
    }
}