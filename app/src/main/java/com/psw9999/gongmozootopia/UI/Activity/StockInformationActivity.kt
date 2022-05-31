package com.psw9999.gongmozootopia.UI.Activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
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
//            val deferredUnderwriters = async(Dispatchers.IO) {
//                underwriters = UnderwriterRepository().getUnderwriters(ipoIndex)
//            }

            // TODO : 3. DB에서 팔로잉데이터 가져오기


            launch {
                loadingOn()
                val stockInfo  = deferredStockInfo.await()
                //val underwriters = deferredUnderwriters.await()
                binding.stockInfo = stockInfo
                //initToolbar()
                loadingOff()
            }
        }
    }

//    private fun addUnderwriterView(underwriters : ArrayList<UnderwriterResponse>) {
//        val constraintSet = ConstraintSet()
//        var beforeUnderwriterID = binding.divisionUnderwritersTitle.id
//        var underwritersTitles = mutableListOf<Int>()
//        var underwriterPair = Pair(0, -1)
//        // underwriters 데이터가 없는 경우 -> "업데이트 예정" textView 써주기
//
//        // 증권사 먼저 동적으로 생성 (문자열이 가장 긴 길이를 알아야 함.)
//        underwriters.forEachIndexed { i, underwriter ->
//            val underwriterName_textView = TextView(this)
//            underwritersTitles.add(View.generateViewId())
//            underwriterName_textView.id = underwritersTitles[i]
//            underwriterName_textView.text = underwriter.underName
//            underwriterName_textView.setTextAppearance(R.style.StockInfo_underwriter)
//            underwriterName_textView.setBackgroundResource(R.drawable.bg_textview_register_underwriter)
//            binding.constraintLayoutUnderwriters.addView(underwriterName_textView)
//            constraintSet.clone(binding.constraintLayoutUnderwriters)
//            constraintSet.connect(underwriterName_textView.id, ConstraintSet.TOP, beforeUnderwriterID, ConstraintSet.BOTTOM, dpToPx(this, 15F).toInt())
//            beforeUnderwriterID = underwriterName_textView.id
//            if (underwriterPair.second < underwriter.underName.length) {
//                underwriterPair = Pair(underwriterName_textView.id, underwriter.underName.length)
//            }
//            constraintSet.applyTo(binding.constraintLayoutUnderwriters)
//        }
//
//        beforeUnderwriterID = binding.divisionUnderwritersTitle.id
//
//        // 수량 동적 생성
//        underwriters.forEachIndexed { i, underwriter ->
//            val underwriterQuantity_textView = TextView(this)
//            underwriterQuantity_textView.id = View.generateViewId()
//            underwriterQuantity_textView.text = "${underwriter.indTotalMax} ~ ${underwriter.indTotalMin} 주"
//            underwriterQuantity_textView.setTextAppearance(R.style.StockInfo_content)
//            binding.constraintLayoutUnderwriters.addView(underwriterQuantity_textView)
//            constraintSet.clone(binding.constraintLayoutUnderwriters)
//            constraintSet.connect(underwriterQuantity_textView.id, ConstraintSet.TOP, underwritersTitles[i], ConstraintSet.TOP, dpToPx(this, 0F).toInt())
//            constraintSet.connect(underwriterQuantity_textView.id, ConstraintSet.BOTTOM, underwritersTitles[i], ConstraintSet.BOTTOM, dpToPx(this, 0F).toInt())
//            constraintSet.connect(underwriterQuantity_textView.id, ConstraintSet.START, underwriterPair.first, ConstraintSet.END, dpToPx(this, 20F).toInt())
//            beforeUnderwriterID = underwriterQuantity_textView.id
//            constraintSet.applyTo(binding.constraintLayoutUnderwriters)
//        }
//    }

    private fun unitCalculate(value : Long) : String {
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
}
