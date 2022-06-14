package com.psw9999.gongmozootopia.UI.Activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Repository.StockInfoRepository
import com.psw9999.gongmozootopia.Repository.UnderwriterRepository
import com.psw9999.gongmozootopia.base.BaseActivity
import com.psw9999.gongmozootopia.data.UnderwriterResponse
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.dpToPx
import com.psw9999.gongmozootopia.data.FollowingResponse
import com.psw9999.gongmozootopia.data.StockInfoResponse
import com.psw9999.gongmozootopia.databinding.ActivityStockInformationBinding
import com.psw9999.gongmozootopia.viewModel.StockInfoViewModel
import com.psw9999.gongmozootopia.viewModel.StockInfoViewModelFactory
import kotlinx.coroutines.*

class StockInformationActivity : BaseActivity() {

    val binding by lazy { ActivityStockInformationBinding.inflate(layoutInflater)}
    private var ipoIndex : Long = -1

    private val viewModelFactory by lazy {
        StockInfoViewModelFactory(application, ipoIndex)
    }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(StockInfoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadDatas()
    }

    private fun addUnderwriterView(underwriters : ArrayList<UnderwriterResponse>, stockKinds : String) {
        underwriters.sortByDescending{it.indTotalMin}
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, dpToPx(10F, this), 0, 0)
        underwriters.forEach { underwriter ->
            binding.linearLayoutUnderwritersTitle.addView(TextView(this).apply{
                setLayoutParams(layoutParams)
                text = underwriter.underName
                gravity = Gravity.CENTER
                setTextAppearance(R.style.StockInfo_content)
            })
            binding.linearLayoutUnderwritersCount.addView(TextView(this).apply{
                setLayoutParams(layoutParams)
                text = when(stockKinds) {
                    "실권주" -> resources.getString(R.string.noneData)
                    else -> resources.getString(R.string.stockAllotment, underwriter.indTotalMin, underwriter.indTotalMax)
                }
                gravity = Gravity.CENTER
                setTextAppearance(R.style.StockInfo_content)
            })
        }
    }

    private fun initToolbar() {
        with(binding.stockInfoAppbar) {
            // 팔로잉 버튼 클릭 이벤트
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_following -> {
                        if (viewModel.isFollowing.value == true) {
                            Snackbar.make(this, "${viewModel.stockInfo!!.stockName}의 팔로잉을 해제하였습니다.", Snackbar.LENGTH_SHORT).show()
                            viewModel.deleteFollowing(viewModel.stockInfo!!.ipoIndex)
                        }else{
                            Snackbar.make(this, "${viewModel.stockInfo!!.stockName}의 팔로잉을 설정하였습니다.", Snackbar.LENGTH_SHORT).show()
                            viewModel.addFollowing(FollowingResponse(ipoIndex= ipoIndex, stockName = viewModel.stockInfo!!.stockName, isFollowing = true))
                            Log.d(TAG, "${viewModel.isFollowing}")
                        }
                        true
                    }
                    else -> true
                }
            }

            // 뒤로가기 버튼 설정
            navigationIcon = getDrawable(R.drawable.baseline_navigate_before_24)
            setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun loadDatas() {
        CoroutineScope(Dispatchers.Main).launch{
            ipoIndex = intent!!.getLongExtra("ipoIndex", -1)
            // 종목의 상세정보 Load
            val deferredStockInfo = async(Dispatchers.IO) {
                StockInfoRepository().getStockInfo(ipoIndex)
            }
            // 주간 증권사 정보 Load
            val deferredUnderwriterInfo = async(Dispatchers.IO) {
                UnderwriterRepository().getUnderwriters(ipoIndex)
            }
            launch {
                loadingOn()
                ipoIndex = intent!!.getLongExtra("ipoIndex", -1)
                viewModel.stockInfo = deferredStockInfo.await()
                viewModel.underwriterInfo= deferredUnderwriterInfo.await()
                //viewModel.getFollowing(ipoIndex)
                binding.viewModel = viewModel
                binding.lifecycleOwner = this@StockInformationActivity
                initToolbar()
                addUnderwriterView(viewModel.underwriterInfo, viewModel.stockInfo.stockKinds)
                loadingOff()
            }
        }
    }

    companion object {
        const val TAG = "STOCK_INFORMATION_ACTIVITY"
        @JvmStatic
        fun unitCalculate(value : Long) : String {
            var tempStr : String = ""
            var value = value
            var share = 1000000000000L
            val unitArray = arrayOf<String>("조","억","만")
            // 조, 억, 만원까지 표시
            repeat(3) { i ->
                var temp = value/share
                if(temp != 0L) {
                    tempStr += " ${temp}${unitArray[i]}"
                    value %= share
                    if (value < 0) value *= -1
                }
                share /= 10000L
            }
            tempStr += "원"
            return tempStr
        }
    }
}

