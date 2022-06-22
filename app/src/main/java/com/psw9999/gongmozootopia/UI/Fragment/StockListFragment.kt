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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.psw9999.gongmozootopia.UI.Activity.StockInformationActivity
import com.psw9999.gongmozootopia.viewModel.ConfigurationViewModel
import com.psw9999.gongmozootopia.databinding.FragmentStockListBinding
import com.psw9999.gongmozootopia.Util.GridViewDecoration
import com.psw9999.gongmozootopia.adapter.StockListPagingAdapter
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.dpToPx
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.viewModel.StockListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StockListFragment : Fragment() {
    private lateinit var binding : FragmentStockListBinding
    private lateinit var stockListAdapter : StockListPagingAdapter
    private lateinit var mContext: Context

    private val stockListViewModel : StockListViewModel by viewModels()
    private val configurationViewModel : ConfigurationViewModel by viewModels()

    private val stockInfoIntent by lazy {
        Intent(mContext, StockInformationActivity::class.java)
    }

    private val TAG = "StockListFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let { it ->
//            stockListViewModel._stockList.value = it.getParcelableArrayList<StockResponse>(STOCK_DATA) as ArrayList<StockResponse>
//        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStockListBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        configurationViewModel.stockFirmData.observe(viewLifecycleOwner, Observer {
//            Log.d("TAG","$it")
//            stockAdapter.setAdapterStockFirmData(it)
//        })

//        configurationViewModel.isForfeitedEnabled.observe(viewLifecycleOwner, Observer { isForfeitedEnabled ->
//            with(stockListViewModel.kindsFilteringArray) {
//                if(isForfeitedEnabled) this[1] = "실권주"
//                else this[1] = ""
//            }
//            stockAdapter.updateStockList(stockListViewModel.stockList)
//            Log.d(TAG, "isForfeitedEnabled")
//        })
//
//        configurationViewModel.isSpacEnabled.observe(viewLifecycleOwner, Observer { isSpacEnabled ->
//            with(stockListViewModel.kindsFilteringArray) {
//                if(isSpacEnabled) this[2] = "스팩주"
//                else this[2] = ""
//            }
//            stockAdapter.updateStockList(stockListViewModel.stockList)
//            Log.d(TAG, "isSpacEnabled")
//        })
//
//        stockListViewModel.followingList.observe(viewLifecycleOwner, Observer { followingList ->
//            stockListViewModel._stockList.value!!.forEach { data ->
//                data.isFollowing = data.ipoIndex in followingList
//            }
//            // 수정하기
//            //stockAdapter.notifyDataSetChanged()
//            Log.d(TAG, "followingList")
//        })
        //onClickSetting()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stockListViewModel._stockList.collectLatest {
                    Log.d(TAG,"$it")
                    (binding.recyclerViewStockList.adapter as StockListPagingAdapter).submitData(it)
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
    }

    private fun onClickSetting() {
        Log.d(TAG, "onClickSetting")
        stockListAdapter.setOnStockClickListener(object : StockListPagingAdapter.OnStockClickListener {
            override fun stockCardClick(ipoIndex: Long) {
                stockInfoIntent.apply {
                    putExtra("ipoIndex", ipoIndex)
                }
                startActivity(stockInfoIntent)
            }

            override fun stockFollowingClick(followingResponse: FollowingResponse) {
                if (followingResponse.isFollowing) {
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