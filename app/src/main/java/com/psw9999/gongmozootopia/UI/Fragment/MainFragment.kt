package com.psw9999.gongmozootopia.UI.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.psw9999.gongmozootopia.adapter.StockListAdapter
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.UI.Activity.StockInformationActivity
import com.psw9999.gongmozootopia.UI.BottomSheet.LoginBottomSheet
import com.psw9999.gongmozootopia.base.BaseApplication
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.helper
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.stockListKey
import com.psw9999.gongmozootopia.data.KakaoLoginStatus
import com.psw9999.gongmozootopia.data.StockListResponse
import com.psw9999.gongmozootopia.databinding.FragmentMainBinding
import com.psw9999.gongmozootopia.util.GridViewDecoration
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MainFragment : Fragment() {
    private lateinit var binding : FragmentMainBinding
    private lateinit var stockAdapter : StockListAdapter
    private lateinit var mContext: Context
    private lateinit var stockList : ArrayList<StockListResponse>

    var followingFilter : Boolean = false
    var stockTypeFilter : String = ""
    var stockStateFilter : String = ""

    private val stockInfoIntent by lazy {
        Intent(mContext, StockInformationActivity::class.java)
    }

    private val preContactStartActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { a_result ->
            if (a_result.resultCode == Activity.RESULT_OK) {
                a_result.data?.let {
                    Log.d("preContactStartActivityResult","RESULT_OK")
                    var pos = it.getIntExtra("itemPos",0)
                    stockList[pos].isFollowing = it.getBooleanExtra("isFollowing",false)
                    stockList[pos].isAlarm = it.getBooleanExtra("isAlarm",false)
                    Log.d("itemUpdate","$pos, ${stockList[pos].isFollowing}, ${stockList[pos].isAlarm}")
                    stockAdapter.setNewData(pos,stockList[pos])
                    stockAdapter.notifyItemChanged(pos)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stockList =
                requireArguments().getParcelableArrayList<StockListResponse>(stockListKey) as ArrayList<StockListResponse>
            for (i in stockList.indices) {
                stockList[i].ipoEndDate?.let { it1 ->
                    var temp = stockScheduleCheck(
                        stockList[i].ipoStartDate,
                        it1, stockList[i].ipoRefundDate, stockList[i].ipoDebutDate
                    ).split(' ')
                    stockList[i].stockState = temp[0]
                    stockList[i].stockDday = temp[1].toInt()
                }
            }
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
        binding.mainActivityAppbar.inflateMenu(R.menu.appbar_action)
        initTabLayout()
        kakaoLoginCheck()
        initStockRecyclerView()
        onClickSetting()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

    private fun initTabLayout() {
        with(binding.tabLayoutMainFragment) {
            addTab(binding.tabLayoutMainFragment.newTab().setText("전체"))
            addTab(binding.tabLayoutMainFragment.newTab().setText("수요예측"))
            addTab(binding.tabLayoutMainFragment.newTab().setText("청약예정"))
            addTab(binding.tabLayoutMainFragment.newTab().setText("환불"))
            addTab(binding.tabLayoutMainFragment.newTab().setText("상장"))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }
            })
        }
    }

    private fun onClickSetting() {
        binding.buttonLogin.setOnClickListener {
            val bottomSheet = LoginBottomSheet()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
        stockAdapter.setOnStockClickListener(object : StockListAdapter.OnStockClickListener {
            override fun stockCardClick(pos: Int) {
                stockInfoIntent.apply {
                    putExtra("itemPos",pos)
                    putExtra("ipoIndex",stockList[pos].ipoIndex)
                    putExtra("isFollowing", stockList[pos].isFollowing)
                    putExtra("isAlarm", stockList[pos].isAlarm)
                }
                preContactStartActivityResult.launch(stockInfoIntent)
            }

            override fun stockFollowingClick(pos: Int) {
                stockList[pos].isFollowing = !(stockList[pos].isFollowing)
                helper.updateSQLiteDB(stockList[pos])
                if (stockList[pos].isFollowing) {
                    Snackbar.make(view!!, "${stockList[pos].stockName}의 팔로잉을 설정하였습니다.",Snackbar.LENGTH_SHORT).show()
                }else{
                    Snackbar.make(view!!, "${stockList[pos].stockName}의 팔로잉을 해제하였습니다.",Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun stockAlarmClick(pos: Int) {
                stockList[pos].isAlarm = !(stockList[pos].isAlarm)
                helper.updateSQLiteDB(stockList[pos])
                if (stockList[pos].isFollowing) {
                    Snackbar.make(view!!, "${stockList[pos].stockName}의 알람을 설정하였습니다.",Snackbar.LENGTH_SHORT).show()
                }else{
                    Snackbar.make(view!!, "${stockList[pos].stockName}의 알람을 해제하였습니다.",Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        binding.chipFollowing.setOnClickListener {
            followingFilter = !(followingFilter)
            // 필터링 메서드 수행
            stockAdapter.filter.filter("$stockStateFilter,$stockTypeFilter,$followingFilter")
        }
        binding.chipGroupStockType.setOnCheckedChangeListener { chipgroup, checkedId ->
            stockTypeFilter = when (checkedId) {
                R.id.chip_IPO -> {
                    "공모주"
                }
                R.id.chip_SPAC -> {
                    "스팩주"
                }
                R.id.chip_rightIssue -> {
                    "실권주"
                }
                else -> {
                    ""
                }

            }
            stockAdapter.filter.filter("$stockStateFilter,$stockTypeFilter,$followingFilter")
            Log.d("checkChip",stockTypeFilter)
        }

    }

    //TODO : onKakaoLoginDoneEvent 함수와 합치기
    private fun kakaoLoginCheck() {
        if(BaseApplication.preferences.isUserLogined) {
            //binding.recyclerViewFollowing.visibility = View.VISIBLE
            binding.cardViewLogin.visibility = View.GONE
        }else{
            binding.cardViewLogin.visibility = View.VISIBLE
            //binding.recyclerViewFollowing.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onKakaoLoginDoneEvent(kakaoLoginStatus : KakaoLoginStatus) {
        if (kakaoLoginStatus.isKakaoLogined){
            //binding.recyclerViewFollowing.visibility = View.VISIBLE
            binding.cardViewLogin.visibility = View.GONE
        }else{
            binding.cardViewLogin.visibility = View.VISIBLE
            //binding.recyclerViewFollowing.visibility = View.GONE
        }
    }

    private fun initStockRecyclerView() {
        stockAdapter = StockListAdapter()
        stockAdapter.stockList = stockList
        if (stockAdapter.stockList === stockList) {
            Log.d("isSame","isSame")
        }
        binding.recyclerViewStock.adapter = stockAdapter
        binding.recyclerViewStock.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStock.addItemDecoration(GridViewDecoration(30))
    }

    private fun stockScheduleCheck(ipoStartDate : String, ipoEndDate : String, ipoRefundDate : String?, ipoDebutDate : String?) : String {
        val now = LocalDate.now()
        val ipoStartDate = ipoStartDate?.let{
            LocalDate.parse(ipoStartDate)
        } ?: now.plusDays(2)
        val ipoEndDate = ipoEndDate?.let{
            LocalDate.parse(ipoEndDate)
        } ?: now.plusDays(3)
        val ipoRefundDate = ipoRefundDate?.let{
            LocalDate.parse(ipoRefundDate)
        } ?: now.plusDays(4)
        val ipoDebutDate = ipoDebutDate?.let {
            LocalDate.parse(ipoDebutDate)
        } ?: now.plusDays(5)
        when {
            now.isBefore(ipoStartDate) -> {
                return "청약 ${ChronoUnit.DAYS.between(now,ipoStartDate)}"
            }
            now.isBefore(ipoEndDate) -> {
                return "청약 0"
            }
            now.isBefore(ipoRefundDate) -> {
                return "환불 ${ChronoUnit.DAYS.between(now,ipoRefundDate)}"
            }
            now.isBefore(ipoDebutDate) -> {
                return "상장 ${ChronoUnit.DAYS.between(now,ipoDebutDate)}"
            }
            else -> {
                return "상장 ${ChronoUnit.DAYS.between(now,ipoDebutDate)}"
            }
        }
    }



}