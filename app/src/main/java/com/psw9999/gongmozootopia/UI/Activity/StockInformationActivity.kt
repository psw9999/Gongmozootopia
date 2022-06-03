package com.psw9999.gongmozootopia.UI.Activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginTop
import com.google.android.material.snackbar.Snackbar
import com.psw9999.gongmozootopia.data.StockFollowingResponse
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.Repository.StockInfoRepository
import com.psw9999.gongmozootopia.Repository.StockScheduleRepository
import com.psw9999.gongmozootopia.base.BaseActivity
import com.psw9999.gongmozootopia.data.StockInfoResponse
import com.psw9999.gongmozootopia.data.UnderwriterResponse
import com.psw9999.gongmozootopia.Repository.UnderwriterRepository
import com.psw9999.gongmozootopia.viewModel.StockFollowingViewModel
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.dpToPx
import com.psw9999.gongmozootopia.databinding.ActivityStockInformationBinding
import kotlinx.coroutines.*
import java.text.NumberFormat

class StockInformationActivity : BaseActivity() {

    val binding by lazy { ActivityStockInformationBinding.inflate(layoutInflater)}
    val stockFollowingViewModel : StockFollowingViewModel by viewModels()
    //lateinit var underwriters : ArrayList<UnderwriterResponse>
    private var ipoIndex : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ipoIndex = intent!!.getLongExtra("ipoIndex", -1)

        CoroutineScope(Dispatchers.Main).launch {
            // 1. 공모가, 공모스케줄, 기업정보 가져오기
            val deferredStockInfo = async(Dispatchers.IO) {
                StockInfoRepository().getStockInfo(ipoIndex)
            }
            // 2. 증권사와 배정수량 가져오기
            val deferredUnderwriters = async(Dispatchers.IO) {
                UnderwriterRepository().getUnderwriters(ipoIndex)
            }

            // TODO : 3. DB에서 팔로잉데이터 가져오기
            //...

            // 4. View Binding
            launch {
                loadingOn()
                val stockInfo = deferredStockInfo.await()
                binding.stockInfo = stockInfo
                addUnderwriterView(deferredUnderwriters.await(), stockInfo.stockKinds)
                loadingOff()
            }
        }
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

//    private fun initToolbar() {
//        with(binding.stockInfoAppbar) {
//            inflateMenu(R.menu.appbar_stock_info)
//            navigationIcon = getDrawable(R.drawable.baseline_navigate_before_24)
//            setNavigationOnClickListener {
//                finish()
//            }
//            with(menu.findItem(R.id.action_following).actionView) {
//                this as ImageButton
//                this.setImageResource(R.drawable.imgbtn_favorit_states)
//                this.setBackgroundResource(R.color.white)
//                if (stockInfo.isFollowing) this.isSelected = true
//                setOnClickListener {
//                    this.isSelected = (!this.isSelected)
//                    stockInfo.isFollowing = (!stockInfo.isFollowing)
//                    if (stockInfo.isFollowing) {
//                        this.isSelected = true
//                        stockFollowingViewModel.addStock(StockFollowingResponse(ipoIndex = stockInfo.ipoIndex, stockName = stockInfo.stockName, isFollowing = true))
//                        Snackbar.make(this, "${stockInfo.stockName}의 팔로잉을 설정하였습니다.", Snackbar.LENGTH_SHORT).show()
//                    }else{
//                        this.isSelected = false
//                        stockFollowingViewModel.deleteStock(stockInfo.ipoIndex)
//                        Snackbar.make(this, "${stockInfo.stockName}의 팔로잉을 해제하였습니다.", Snackbar.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//    }

    companion object {
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
