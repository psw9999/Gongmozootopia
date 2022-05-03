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
import com.psw9999.gongmozootopia.Data.StockFollowingResponse
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.UI.Activity.StockInformationActivity
import com.psw9999.gongmozootopia.UI.BottomSheet.LoginBottomSheet
import com.psw9999.gongmozootopia.ViewModel.ConfigurationViewModel
import com.psw9999.gongmozootopia.Data.StockResponse
import com.psw9999.gongmozootopia.UI.Activity.LoadingActivity.Companion.STOCK_DATA
import com.psw9999.gongmozootopia.ViewModel.StockFollowingViewModel
import com.psw9999.gongmozootopia.databinding.FragmentMainBinding
import com.psw9999.gongmozootopia.Util.GridViewDecoration

class MainFragment : Fragment() {
    private lateinit var binding : FragmentMainBinding
    private lateinit var stockAdapter : StockListAdapter
    private lateinit var mContext: Context
    private lateinit var stockData : ArrayList<StockResponse>
    private lateinit var filterdStockData : ArrayList<StockResponse>
    private var filteringList = arrayOf("공모주", "실권주", "스팩주")

    private val configurationViewModel : ConfigurationViewModel by viewModels()
    private val stockFollowingViewModel : StockFollowingViewModel by viewModels()

    private val stockInfoIntent by lazy {
        Intent(mContext, StockInformationActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
            if(it) filteringList[1] = "실권주"
            else filteringList[1] = ""
        })

        configurationViewModel.isSpacEnabled.observe(viewLifecycleOwner, Observer {
            if(it) filteringList[2] = "스팩주"
            else filteringList[1] = ""
        })

        stockFollowingViewModel.stockFollowingIndexData.observe(viewLifecycleOwner, Observer { stockFollowingIndex ->
            stockData.forEach { data ->
                data.isFollowing = data.ipoIndex in stockFollowingIndex
            }
            stockAdapter.setAdapterStockFollowingData(stockData)
        })

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
                    putExtra("isFollowing",stockData[pos].isFollowing)
                    putExtra("ipoIndex",stockData[pos].ipoIndex)
                }
                startActivity(stockInfoIntent)
            }

            override fun stockFollowingClick(stockFollowingResponse: StockFollowingResponse) {
                if (stockFollowingResponse.isFollowing) {
                    stockFollowingViewModel.addStock(stockFollowingResponse)
                    Snackbar.make(view!!, "${stockFollowingResponse.stockName}의 팔로잉을 설정하였습니다.",Snackbar.LENGTH_SHORT).show()
                }else{
                    stockFollowingViewModel.deleteStock(stockFollowingResponse.ipoIndex)
                    Snackbar.make(view!!, "${stockFollowingResponse.stockName}의 팔로잉을 해제하였습니다.",Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        binding.mainActivityAppbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_filter -> {
                    true
                }
                else -> true
            }
        }
    }

    private fun initStockRecyclerView() {
        stockAdapter = StockListAdapter()
        stockAdapter.stockData = stockData
        binding.recyclerViewStock.adapter = stockAdapter
        binding.recyclerViewStock.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStock.addItemDecoration(GridViewDecoration(30))
    }
}