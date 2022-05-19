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
import com.psw9999.gongmozootopia.base.BaseActivity
import com.psw9999.gongmozootopia.data.StockInfoResponse
import com.psw9999.gongmozootopia.data.UnderwriterResponse
import com.psw9999.gongmozootopia.Repository.UnderwriterRepository
import com.psw9999.gongmozootopia.viewModel.StockFollowingViewModel
import com.psw9999.gongmozootopia.base.BaseApplication.Companion.dpToPx
import com.psw9999.gongmozootopia.databinding.ActivityStockInformationBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockInformationActivity : BaseActivity() {

    val binding by lazy { ActivityStockInformationBinding.inflate(layoutInflater)}
    val stockFollowingViewModel : StockFollowingViewModel by viewModels()
    lateinit var stockInfo : StockInfoResponse
    lateinit var underwriters : ArrayList<UnderwriterResponse>
    private var ipoIndex : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadingOn()
        ipoIndex = intent!!.getLongExtra("ipoIndex", -1)
        CoroutineScope(Dispatchers.IO).launch() {
            stockInfo = StockInfoRepository().getStockInfo(ipoIndex)
            underwriters = UnderwriterRepository().getUnderwriters(ipoIndex)

            withContext(Dispatchers.Main) {
                with(stockInfo) {
                    isFollowing = intent!!.getBooleanExtra("isFollowing", false)
                    initToolbar()
                    if (ipoPrice == 0L) binding.textViewConfirmStockPrice.text = "미정"
                    else binding.textViewConfirmStockPrice.text = "${ipoPrice}원"
                    binding.textViewStockPriceBand.text = "${ipoPriceLow} ~ ${ipoPriceHigh}원"
                    binding.textViewIpoForecastDay.text = ipoForecastDate
                    binding.textViewIpoStartDay.text = ipoStartDate
                    binding.textViewIpoDebutDay.text = ipoDebutDate
                    binding.textViewIpoRefundDay.text = ipoRefundDate
                    if (stockKinds == "스팩주") binding.cardViewCompanyInfo.visibility = View.GONE
                    else {
                        if (sales == 0L) binding.textViewCompanySales.text = "확인필요"
                        else binding.textViewCompanySales.text = unitCalculate(sales)
                        if (profits == 0L) binding.textViewCompanyProfit.text = "확인필요"
                        else binding.textViewCompanyProfit.text = unitCalculate(profits)
                    }
                    binding.textViewSector.text = sector
                    binding.textViewStockMarket.text = stockExchange
                    addUnderwriterView(underwriters)
                    loadingOff()
                }
            }
        }
    }

    private fun addUnderwriterView(underwriters : ArrayList<UnderwriterResponse>) {
        val constraintSet = ConstraintSet()
        var beforeUnderwriterID = binding.divisionUnderwritersTitle.id
        var underwriterPair = Pair(0, -1)
        // underwriters 데이터가 없는 경우 -> "업데이트 예정" textView 써주기

        // 증권사 먼저 동적으로 생성 (문자열이 가장 긴 길이를 알아야 함.)
        underwriters.forEach { underwriter ->
            val underwriterName_textView = TextView(this)
            underwriterName_textView.id = View.generateViewId()
            underwriterName_textView.text = underwriter.underName
            underwriterName_textView.setTextAppearance(R.style.StockInfo_underwriter)
            underwriterName_textView.setBackgroundResource(R.drawable.bg_textview_register_underwriter)
            binding.constraintLayoutUnderwriters.addView(underwriterName_textView)
            constraintSet.clone(binding.constraintLayoutUnderwriters)
            constraintSet.connect(underwriterName_textView.id, ConstraintSet.TOP, beforeUnderwriterID, ConstraintSet.BOTTOM, dpToPx(this, 15F).toInt())
            beforeUnderwriterID = underwriterName_textView.id
            if (underwriterPair.second < underwriter.underName.length) {
                underwriterPair = Pair(underwriterName_textView.id, underwriter.underName.length)
            }
            constraintSet.applyTo(binding.constraintLayoutUnderwriters)
        }

        beforeUnderwriterID = binding.divisionUnderwritersTitle.id
        
        // 수량 동적 생성
        underwriters.forEach { underwriter ->
            val underwriterQuantity_textView = TextView(this)
            underwriterQuantity_textView.id = View.generateViewId()
            underwriterQuantity_textView.text = underwriter.indTotalMax.toString()
            underwriterQuantity_textView.setTextAppearance(R.style.StockInfo_content)
            binding.constraintLayoutUnderwriters.addView(underwriterQuantity_textView)
            constraintSet.clone(binding.constraintLayoutUnderwriters)
            constraintSet.connect(underwriterQuantity_textView.id, ConstraintSet.TOP, beforeUnderwriterID, ConstraintSet.BOTTOM, dpToPx(this, 15F).toInt())
            constraintSet.connect(underwriterQuantity_textView.id, ConstraintSet.START, underwriterPair.first, ConstraintSet.END, dpToPx(this, 20F).toInt())
            beforeUnderwriterID = underwriterQuantity_textView.id
            constraintSet.applyTo(binding.constraintLayoutUnderwriters)
        }

    }

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

    private fun initToolbar() {
        with(binding.stockInfoAppbar) {
            inflateMenu(R.menu.appbar_stock_info)
            navigationIcon = getDrawable(R.drawable.baseline_navigate_before_24)
            setNavigationOnClickListener {
                finish()
            }
            title = stockInfo.stockName
            isTitleCentered = true
            with(menu.findItem(R.id.action_following).actionView) {
                this as ImageButton
                this.setImageResource(R.drawable.imgbtn_favorit_states)
                this.setBackgroundResource(R.color.white)
                if (stockInfo.isFollowing) this.isSelected = true
                setOnClickListener {
                    this.isSelected = (!this.isSelected)
                    stockInfo.isFollowing = (!stockInfo.isFollowing)
                    if (stockInfo.isFollowing) {
                        this.isSelected = true
                        stockFollowingViewModel.addStock(StockFollowingResponse(ipoIndex = stockInfo.ipoIndex, stockName = stockInfo.stockName, isFollowing = true))
                        Snackbar.make(this, "${stockInfo.stockName}의 팔로잉을 설정하였습니다.", Snackbar.LENGTH_SHORT).show()
                    }else{
                        this.isSelected = false
                        stockFollowingViewModel.deleteStock(stockInfo.ipoIndex)
                        Snackbar.make(this, "${stockInfo.stockName}의 팔로잉을 해제하였습니다.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
