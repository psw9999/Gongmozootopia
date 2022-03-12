 package com.psw9999.ipo_alarm.UI.Fragment.MainFrag

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.ipo_alarm.Adapter.FollowingAdapter
import com.psw9999.ipo_alarm.UI.BottomSheet.LoginBottomSheet
import com.psw9999.ipo_alarm.data.FollowingResponse
import com.psw9999.ipo_alarm.data.StockListResponse
import com.psw9999.ipo_alarm.databinding.FragmentMainFirstBinding
import com.psw9999.ipo_alarm.util.sqliteHelper
import org.greenrobot.eventbus.EventBus

 class MainFirstFragment : Fragment() {

    var stockList : MutableList<StockListResponse> = mutableListOf()
    var testFollowingList : MutableList<FollowingResponse> = mutableListOf()

    private lateinit var binding : FragmentMainFirstBinding
    private lateinit var helper : sqliteHelper
    private lateinit var followingAdapter : FollowingAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("MainFirstFragment","onCreateView")
        binding = FragmentMainFirstBinding.inflate(inflater,container,false)
        onClickSetting()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        helper = sqliteHelper(context,"IPO_stock",1)
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

    private fun onClickSetting() {
        binding.buttonLogin.setOnClickListener {
            val bottomSheet = LoginBottomSheet()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

//    private fun testRecyclerView() {
//        stockList.add(
//            StockData(
//                ipoIndex = 12345,
//                stockName = "인카금융서비스",
//                stockExchange = "코스닥",
//                stockKinds = "공모주",
//                ipoStartDate = "2022-02-07",
//                ipoEndDate = "2022-02-08",
//                ipoRefundDate = "2022-02-10",
//                ipoDebutDate = "2022-02-16",
//                underwriter = "한국투자증권",
//                tag = null,
//                stockState = stockScheduleCheck("2022-02-07","2022-02-08","2022-02-10","2022-02-16")
//            )
//        )

        //testStockList = helper.selectSQLiteDB(testStockList)
        // 최초 DB 테이블이 없을때 실행하여 생성
//        for (testStock in testStockList) {
//            helper.insertSQLiteDB(testStock)
//        }
    }

//    private fun initRecyclerView() {
//        var stockAdapter = StockListAdapter()
//        stockAdapter.stockData = stockList
//        binding.recyclerViewStock.adapter = stockAdapter
//        binding.recyclerViewStock.layoutManager = LinearLayoutManager(requireContext())
//        binding.recyclerViewStock.addItemDecoration(GridViewDecoration(30))

//        stockAdapter.setOnStockFollowingClickListener(object : StockAdapter.OnStockFollowingClickListener {
//            override fun stockFollowingClick(pos: Int) {
//                testStockList[pos].isFollowingEnabled = !(testStockList[pos].isFollowingEnabled)
//                helper.updateSQLiteDB(testStockList[pos])
//
//                if(testStockList[pos].isFollowingEnabled) {
//                    var tempFollowingData : FollowingData = FollowingData(
//                            stockCode = testStockList[pos].stockCode,
//                            stockName = testStockList[pos].stockName,
//                            stockDday = testStockList[pos].stockDday,
//                            IPO_startDay = testStockList[pos].IPO_startDay,
//                            isFollowing = testStockList[pos].isFollowingEnabled)
//                    if(followingAdapter.unvisibleData.size > 0) {
//                        followingAdapter.unvisibleData.add(tempFollowingData)
//                    }else{
//                        testFollowingList.add(tempFollowingData)
//                        followingAdapter.notifyItemInserted(testFollowingList.size)
//                    }
//                }else{
//                    if(followingAdapter.unvisibleData.size > 0) {
//                        for(index in followingAdapter.unvisibleData.indices) {
//                            if (testStockList[pos].stockCode == followingAdapter.unvisibleData[index].stockCode){
//                                followingAdapter.unvisibleData.removeAt(index)
//                                break
//                            }
//                        }
//                    }else{
//                        for(index in testFollowingList.indices) {
//                            if (testStockList[pos].stockCode == testFollowingList[index].stockCode){
//                                testFollowingList.removeAt(index)
//                                followingAdapter.notifyItemRemoved(index+1)
//                                break
//                            }
//                        }
//                    }
//                }
//            }
//        })

//        stockAdapter.setOnStockAlarmClickListener(object : StockAdapter.OnStockAlarmClickListener {
//            override fun stockAlarmClick(pos: Int) {
//                // DB 업데이트
//                testStockList[pos].isAlarmEnabled = !(testStockList[pos].isAlarmEnabled)
//                helper.updateSQLiteDB(testStockList[pos])
//            }
//        })
    //}

//    private fun initFollowingRecyclerView(){
//        followingAdapter = FollowingAdapter()
////        for (testStock in testStockList) {
////            if (testStock.isFollowingEnabled) {
////                testFollowingList.add(
////                    FollowingData(
////                        stockCode = testStock.stockCode,
////                        stockName = testStock.stockName,
////                        stockDday = testStock.stockDday,
////                        IPO_startDay = testStock.IPO_startDay,
////                        isFollowing = testStock.isFollowingEnabled))
////            }
////        }
//        followingAdapter.followingData = testFollowingList
//        binding.recyclerViewFollowing.adapter = followingAdapter
//        binding.recyclerViewFollowing.layoutManager = LinearLayoutManager(requireContext())
//    }


//}