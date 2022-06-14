package com.psw9999.gongmozootopia.UI.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.psw9999.gongmozootopia.Adapter.StockListAdapter
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.UI.Activity.StockInformationActivity
import com.psw9999.gongmozootopia.UI.BottomSheet.LoginBottomSheet
import com.psw9999.gongmozootopia.viewModel.ConfigurationViewModel
import com.psw9999.gongmozootopia.data.StockResponse
import com.psw9999.gongmozootopia.UI.Activity.LoadingActivity.Companion.STOCK_DATA
import com.psw9999.gongmozootopia.viewModel.FollowingViewModel
import com.psw9999.gongmozootopia.databinding.FragmentMainBinding
import com.psw9999.gongmozootopia.Util.GridViewDecoration

class MainFragment : Fragment() {
    private lateinit var binding : FragmentMainBinding
    private lateinit var stockAdapter : StockListAdapter
    private lateinit var mContext: Context
    private lateinit var stockData : ArrayList<StockResponse>
    private lateinit var filterdStockData : List<StockResponse>
    private var kindFilteringList = arrayOf("공모주", "실권주", "스팩주")
    private var followingFilterEnabled = false

    private val configurationViewModel : ConfigurationViewModel by viewModels()

    private val stockInfoIntent by lazy {
        Intent(mContext, StockInformationActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { it ->
            stockData = it.getParcelableArrayList<StockResponse>(STOCK_DATA) as ArrayList<StockResponse>
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater,container,false)
        binding.mainActivityAppbar.inflateMenu(R.menu.appbar_main)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configurationViewModel.stockFirmData.observe(viewLifecycleOwner, Observer {
            stockAdapter.setAdapterStockFirmData(it)
        })

        configurationViewModel.isForfeitedEnabled.observe(viewLifecycleOwner, Observer {
            if(it) kindFilteringList[1] = "실권주"
            else kindFilteringList[1] = ""
            stockFiltering()
        })

        configurationViewModel.isSpacEnabled.observe(viewLifecycleOwner, Observer {
            if(it) kindFilteringList[2] = "스팩주"
            else kindFilteringList[2] = ""
            stockFiltering()
        })

//        //TODO : 필터링된 데이터에 팔로잉을 넣는게 맞는지 확인필요. (나중에 필터링 풀리면 팔로잉된게 안보일까봐)
//        followingViewModel.stockFollowingIndexData.observe(viewLifecycleOwner, Observer { stockFollowingIndex ->
//            filterdStockData.forEach { data ->
//                data.isFollowing = data.ipoIndex in stockFollowingIndex
//            }
//            stockAdapter.updateStockData(filterdStockData)
//        })
        initStockRecyclerView()
        onClickSetting()
    }

    private fun onClickSetting() {
        binding.buttonLogin.setOnClickListener {
            val bottomSheet = LoginBottomSheet()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
        stockAdapter.setOnStockClickListener(object : StockListAdapter.OnStockClickListener {
            override fun stockCardClick(pos: Int) {
                stockInfoIntent.apply {
                    putExtra("ipoIndex",filterdStockData[pos].ipoIndex)
                }
                startActivity(stockInfoIntent)
            }

            override fun stockFollowingClick(followingResponse: FollowingResponse) {
                if (followingResponse.isFollowing) {
                    //followingViewModel.addStock(followingResponse)
                    Snackbar.make(view!!, "${followingResponse.stockName}의 팔로잉을 설정하였습니다.",Snackbar.LENGTH_SHORT).show()
                }else{
                    //followingViewModel.deleteStock(followingResponse.ipoIndex)
                    Snackbar.make(view!!, "${followingResponse.stockName}의 팔로잉을 해제하였습니다.",Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        binding.mainActivityAppbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_followingFilter -> {
                    followingFilterEnabled = !followingFilterEnabled
                    if(followingFilterEnabled) it.setIcon(R.drawable.star_active)
                    else it.setIcon(R.drawable.star_border)
                    stockFiltering()
                    true
                }
                else -> true
            }
        }
    }

    private fun initStockRecyclerView() {
        stockAdapter = StockListAdapter()
        stockFiltering()
        stockAdapter.stockData = filterdStockData
        binding.recyclerViewStock.adapter = stockAdapter
        binding.recyclerViewStock.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStock.addItemDecoration(GridViewDecoration(30))
    }

    private fun stockFiltering() {
        filterdStockData = stockData.filter { data->
            //data.stockKinds in kindFilteringList && (data.isFollowing || !followingFilterEnabled)
            data.stockKinds in kindFilteringList && (!followingFilterEnabled)
        }
        stockAdapter.updateStockData(filterdStockData)
    }
}